<template>
    <section>
      <div>
        <input type="file" id="file-input" />
        <!--        <v-icon id="play" icon="mdi-play-circle" />-->
        <!--        <v-icon id="stop" icon="mdi-pause-circle" />-->
        <v-btn id="play" v-on:click="start()" >Play</v-btn>

        <v-btn id="stop" v-on:click="stop()">Stop</v-btn>
      </div>

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
import { IFCSENSOR, IFCSPACE, IFCOPENINGELEMENT, IFCFURNISHINGELEMENT, IFCDISTRIBUTIONCONTROLELEMENT, IFCSENSORTYPE } from 'web-ifc';
import {IfcAPI} from "three/examples/jsm/loaders/ifc/web-ifc-api";

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
          invisibleMat: new MeshLambertMaterial({
            transparent: true,
            opacity: 0.6,
            color: 0xff0000
          }),
          preSelectMat: new MeshLambertMaterial({
            transparent: true,
            opacity: 0.3,
            color: 0xff0000,
            depthTest: false,
          }),
          preSelectMatBlue: new MeshLambertMaterial({
            transparent: true,
            opacity: 0.3,
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
            if (relIDs.type === "IFCSPACE") {
                const sensorList = [];
                rooms.push({roomId:relIDs.expressID, sensors:sensorList});
            }
            for (let component in relIDs.children) {
                if (relIDs.type === "IFCSPACE" && relIDs.children[component].type === "IFCDISTRIBUTIONCONTROLELEMENT") {
                    const sensor = await manager.getItemProperties(modelID, relIDs.children[component].expressID);
                    rooms[rooms.length-1].sensors.push({sensorIFCid:relIDs.children[component].expressID, sensorDataSetId:sensor.Name.value});
                }
               await this.getSensors(relIDs.children[component], rooms, manager, modelID);
            }
        },
        start: function() {
          axios
              .post('http://localhost:8082/api/start')
              .then(response => (console.log(response)));
        },
        stop: function() {
          axios
              .post('http://localhost:8082/api/stop')
              .then(response => (console.log(response)));
        }
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
            console.log(response["sensorIfcID"]);
            if (response["sensorIfcID"] === 283) {
              if (response["value"] === 20) {
                changeColor(this.structure, 201, response["sensorIfcID"], this.preSelectMat, this.preSelectMatBlue, 1);
              } else {
                changeColor(this.structure, 201, response["sensorIfcID"], this.preSelectMatBlue, this.preSelectMat, 1);
              }
            }
            if (response["sensorIfcID"] === 722) {
              if (response["value"] === 20) {
                changeColor(this.structure, 234, response["sensorIfcID"], this.preSelectMat, this.preSelectMatBlue, 2);
              } else {
                changeColor(this.structure, 234, response["sensorIfcID"], this.preSelectMatBlue, this.preSelectMat, 2);
              }
            }

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

      const changeColor = (relIDs, roomId, sensorId, material, previousMaterial, groupId) => {
        const manager = this.viewer.IFC.loader.ifcManager;
        manager.removeSubset(this.model.modelID, previousMaterial, groupId);
        manager.createSubset({
          modelID: this.model.modelID,
          ids: [roomId],
          material: material,
          scene: this.viewer.context.getScene(),
          removePrevious: false,
          customID: groupId
        });
/*        for (let component in relIDs.children) {
          if (relIDs.expressID === roomId && relIDs.children[component].type === "IFCSPACE") {
            const manager = this.viewer.IFC.loader.ifcManager;
            manager.removeSubset(this.model.modelID, previousMaterial, groupId);
            manager.createSubset({
              modelID: this.model.modelID,
              ids: [relIDs.children[component].expressID],
              material: material,
              scene: this.viewer.context.getScene(),
              removePrevious: false,
              customID: groupId
            });
          }
          changeColor(relIDs.children[component], roomId, sensorId, material, previousMaterial);
        }*/
      }
    },
    mounted() {
      const container = document.getElementById('model');
      const viewer = new IfcViewerAPI({ container });
      this.viewer = viewer;
      viewer.axes.setAxes();
      viewer.grid.setGrid();
      viewer.IFC.setWasmPath('../IFCjs/');
     // const ifcapi = new IfcAPI();
/*      viewer.IFC.loader.ifcManager.parser.setupOptionalCategories({
        [IFCSPACE]: false,
        [IFCOPENINGELEMENT]: false
      });*/

      const input = document.getElementById("file-input");

      input.addEventListener("change",

          async (changed) => {
            //await ifcapi.Init();
            const file = changed.target.files[0];
            const ifcURL = URL.createObjectURL(file);
            const model = await viewer.IFC.loadIfcUrl(ifcURL);
            this.model = model;
            const structure = await this.showStructure(viewer, model.modelID);
            this.structure = structure;
            console.log(await viewer.IFC.getProperties(model.modelID, 283, true));
            const spaces = await viewer.IFC.getAllItemsOfType(model.modelID, IFCSPACE, true);
            const manager = this.viewer.IFC.loader.ifcManager;
            for (const space in spaces) {

              //console.log(await ifcapi.GetGeometry(model.modelID, spaces[space]));
              manager.createSubset({
                modelID: this.model.modelID,
                ids: [spaces[space]],
                material: this.invisibleMat,
                scene: this.viewer.context.getScene(),
                removePrevious: false,
              });
            }
            let json = {rooms:[]};
            await this.getSensors(structure, json.rooms, manager, model.modelID);
            console.log(JSON.stringify(json));
            //model.visible = false;

            const modelCopy = new Mesh(
                model.geometry,
                new MeshLambertMaterial({
                  transparent: true,
                  opacity: 1,
                  color: 0x77aaff,
                })
            );

            const scene = this.viewer.context.getScene();

            scene.add(model);
            scene.add(modelCopy);
/*            manager.createSubset({
              modelID: this.model.modelID,
              ids: [201],
              material: this.preSelectMat,
              scene: this.viewer.context.getScene(),
              removePrevious: false,
            });*/
            axios
                .post('http://localhost:8082/api/rooms', json)
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
  position: relative;
  /*left: 10%;*/
  /*top: 10%;*/
  z-index: 10;
}

#play{
  position: relative;
  color: blue;
  margin: 0.5em 0.5em 0.5em;
  z-index: 10;
}

#stop{
  position: relative;
  color: blue;
  margin: 0.5em 0.5em 0.5em;
  z-index: 10;
}

#properties-text {
    position: absolute;
    left: 0%;
    bottom: 0%;
    z-index: 100;
}
</style>
