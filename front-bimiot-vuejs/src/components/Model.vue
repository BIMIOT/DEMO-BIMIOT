<template>
    <section>
      <div>
        <input type="file" id="file-input" />
<!--        <v-icon id="play" icon="mdi-play-circle" />-->
<!--        <v-icon id="stop" icon="mdi-pause-circle" />-->
        <v-btn id="play" onclick="this.created" >Play</v-btn>

        <v-btn id="stop">Stop</v-btn>
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
import {CSS2DRenderer} from "three/examples/jsm/renderers/CSS2DRenderer";
import * as SockJS from 'sockjs-client';
import * as StompJs from '@stomp/stompjs';

export default {
    name: 'Model',
    props: ['token', 'projectId', 'discipline'],
    data() {
        return {
            entityData: '',
            client: undefined,
        }
    },
    methods: {
        showStructure: async function(viewer, modelID) {
            const manager = viewer.IFC.loader.ifcManager;
            const relIDs = await manager.getSpatialStructure(modelID);
            console.log(relIDs);
            return relIDs;
        },
        sendMessage: function(message) {
            console.log(this.client);
            this.client.send(message);
        },
        getSensors: async function(relIDs, rooms, manager, modelID) {
            if (relIDs.type === "IFCBUILDINGSTOREY") {
                const sensorList = [];
                rooms.push({roomId:relIDs.expressID, sensors:sensorList});
            }
            for (let component in relIDs.children) {
                if (relIDs.type === "IFCBUILDINGSTOREY" && relIDs.children[component].type === "IFCFURNISHINGELEMENT") {
                    const sensor = await manager.getItemProperties(modelID, relIDs.children[component].expressID);
                    console.log(sensor);
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
              let labelRenderer = new CSS2DRenderer();
              labelRenderer.setSize( window.innerWidth, window.innerHeight );
              labelRenderer.domElement.style.position = 'absolute';
              labelRenderer.domElement.style.top = '0px';
              labelRenderer.domElement.style.pointerEvents = 'none';
            }
            this.changeColor(relIDs.children[component], roomId, sensorId, material, manager, scene, modelID);
          }
        }



    },
    created: function() {
        console.log("Starting connection to WebSocket Server");
        let client = new StompJs.Client({
          brokerURL: 'ws://localhost:8083/sensors-data-endpoint',
          debug: function (str) {
            console.log(str);
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
            return new SockJS('http://localhost:8083/sensors-data-endpoint');
          };
        }


        client.onConnect = function (frame) {
          // Do something, all subscribes must be done is this callback
          // This is needed because this will be executed after a (re)connect
          client.subscribe('/data/sensors', function (greeting) {
            console.log(JSON.parse(greeting.body));
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
      viewer.axes.setAxes();
      viewer.grid.setGrid();
      viewer.IFC.setWasmPath('../IFCjs/');
      const textElement = document.createElement('p');
      textElement.innerText = 'This is some text';
      document.querySelector("#model").appendChild(textElement);

      const input = document.getElementById("file-input");

      input.addEventListener("change",

          async (changed) => {
            const file = changed.target.files[0];
            const ifcURL = URL.createObjectURL(file);
            const model = await viewer.IFC.loadIfcUrl(ifcURL);
            const structure = await this.showStructure(viewer, model.modelID);
            //console.log(viewer.context.getDomElement());
            this.sendMessage('hello');
            let json = {rooms:[]};
            await this.getSensors(structure, json.rooms, viewer.IFC.loader.ifcManager, model.modelID);
            console.log(JSON.stringify(json));
            const preSelectMat = new MeshLambertMaterial({
              transparent: true,
              opacity: 0.6,
              color: 0xff0000,
              depthTest: false,
            });
            const preSelectMatBlue = new MeshLambertMaterial({
              transparent: true,
              opacity: 0.6,
              color: 0x00FFFF,
              depthTest: false,
            });
            const scene = viewer.context.getScene();
            const manager = viewer.IFC.loader.ifcManager;
            setTimeout(() => {  this.changeColor(structure, 138, 1, preSelectMat, manager, scene, model.modelID); }, 2000);
            //setTimeout(() => {  this.changeColor(structure, 138, 1, preSelectMatBlue, manager, scene, model.modelID); }, 4000);
          },

          false
      );

      axios
        .get('https://api.coindesk.com/v1/bpi/currentprice.json')
        .then(response => (console.log(response)));
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
