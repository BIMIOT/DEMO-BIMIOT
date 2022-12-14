<template>
    <section>
        <input type="file" id="file-input" />
        <p id="properties-text">
            ID: 
            {{ entityData }}
        </p>
        <div id="model" />
    </section>
</template>

<script>
import { IfcViewerAPI } from 'web-ifc-viewer';
import { MeshLambertMaterial } from "three";
import axios from 'axios';
import * as SockJS from 'sockjs-client';
import * as StompJs from '@stomp/stompjs';
import { IFCSENSOR } from 'web-ifc';

export default {
    name: 'Model',
    props: ['token', 'projectId', 'discipline'],
    data() {
        return {
            entityData: '',
            client: undefined,
            viewer: undefined,
            model: undefined,
            structure: undefined,
          preSelectMat: new MeshLambertMaterial({
            transparent: true,
            opacity: 0.6,
            color: 0xff0000,
            depthTest: false,
          }),
          preSelectMatBlue: new MeshLambertMaterial({
            transparent: true,
            opacity: 0.6,
            color: 0x00FFFF,
            depthTest: false,
          }),
        }
    },
    methods: {
        showStructure: async function(viewer, modelID) {
            const manager = viewer.IFC.loader.ifcManager;
            const relIDs = await manager.getSpatialStructure(modelID);
            console.log(relIDs);
            return relIDs;
        },
        getSensors: async function(relIDs, rooms, manager, modelID) {
            if (relIDs.type === "IFCBUILDINGSTOREY") {
                const sensorList = [];
                rooms.push({roomId:relIDs.expressID, sensors:sensorList});
            }
            for (let component in relIDs.children) {
                if (relIDs.type === "IFCBUILDINGSTOREY" && relIDs.children[component].type === "IFCFURNISHINGELEMENT") {
                    const sensor = await manager.getItemProperties(modelID, relIDs.children[component].expressID);
                    rooms[rooms.length-1].sensors.push({sensorIFCid:relIDs.children[component].expressID, sensorDataSetId:sensor.Name.value});
                }
               await this.getSensors(relIDs.children[component], rooms, manager, modelID);
            }
        },
        changeColor: function(relIDs, roomId, sensorId, material, manager, scene, modelID) {
          for (let component in relIDs.children) {
            if (relIDs.expressID === roomId && relIDs.children[component].type === "IFCSLAB") {
              manager.createSubset({
                modelID: modelID,
                ids: [relIDs.children[component].expressID],
                material: material,
                scene: scene,
                removePrevious: true,
              });
            }
            this.changeColor(relIDs.children[component], roomId, sensorId, material, manager, scene, modelID);
          }
        },

    },
    created: function() {
        console.log("Starting connection to WebSocket Server");
        let client = new StompJs.Client({
          brokerURL: 'ws://localhost:8082/sensors-data-endpoint',
          debug: function (str) {
            //console.log(str);
          },
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
        });

        // Fallback code
        if (typeof WebSocket !== 'function') {
          // For SockJS you need to set a factory that creates a new SockJS instance
          // to be used for each (re)connect
          client.webSocketFactory = function () {
            // Note that the URL is different from the WebSocket URL
            return new SockJS('http://localhost:8082/sensors-data-endpoint');
          };
        }

        client.onConnect = (frame) => {
          // Do something, all subscribes must be done is this callback
          // This is needed because this will be executed after a (re)connect
          client.subscribe('/data/sensors', (greeting) => {
            const response = JSON.parse(greeting.body);
            if (this.model === undefined) {
              return;
            }
            const scene = this.viewer.context.getScene();
            const manager = this.viewer.IFC.loader.ifcManager;
            this.changeColor(this.structure, 138, response["sensorIfcID"], response["value"] === 20 ? this.preSelectMat : this.preSelectMatBlue, manager, scene, this.model.modelID);
          });

          console.log("Successfully subscribed to the backend server...");
        };

        client.onStompError = function (frame) {
          // Will be invoked in case of error encountered at Broker
          // Bad login/passcode typically will cause an error
          // Complaint brokers will set `message` header with a brief message. Body may contain details.
          // Compliant brokers will terminate the connection after any error
          console.log('Broker reported error: ' + frame.headers['message']);
          console.log('Additional details: ' + frame.body);
        };

        client.activate();
        this.client = client;
    },
    mounted() {
      const container = document.getElementById('model');
      const viewer = new IfcViewerAPI({ container });
      this.viewer = viewer;
      viewer.axes.setAxes();
      viewer.grid.setGrid();
      viewer.IFC.setWasmPath('../IFCjs/');

      const input = document.getElementById("file-input");

      input.addEventListener("change",

          async (changed) => {
            const file = changed.target.files[0];
            const ifcURL = URL.createObjectURL(file);
            const model = await viewer.IFC.loadIfcUrl(ifcURL);
            this.model = model;
            const structure = await this.showStructure(viewer, model.modelID);
            this.structure = structure;
            //console.log(await viewer.IFC.getAllItemsOfType(model.modelID, IFCSENSOR, true));
            let json = {rooms:[]};
            await this.getSensors(structure, json.rooms, viewer.IFC.loader.ifcManager, model.modelID);
            console.log(JSON.stringify(json));
            axios
                .post('http://localhost:8082/api/rooms', json)
                .then(response => (console.log(response)));
            axios
                .post('http://localhost:8082/api/start')
                .then(response => (console.log(response)));
          },

          false
      );
    },
}
</script>

<style>
#model {
    position: absolute;
    left: 0%;
    top: 0%;
    width: 100% !important;
    height: 100% !important;
}

#file-input {
    position: absolute;
    left: 0%;
    top: 0%;
    z-index: 100;
}

#properties-text {
    position: absolute;
    left: 0%;
    bottom: 0%;
    z-index: 100;
}
</style>
