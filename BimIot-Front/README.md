# Vue IFC.js Example

A basic example for running IFC.js within Vue.

For more information about IFC.js, see the following link:

###### IFC.js
https://https://ifcjs.github.io

<p align="center">
  <img width="75px" src="https://ifcjs.github.io/info/assets/images/logo-a326242dd945bcc271d193f7e6d2f054.png">
</p>

## Project setup

First install all node modules with the command below
```
npm i
```
Then move `IFCWorker.js` & `web-ifc.wasm` to `public\IFCjs` with the following command
```
npm run preserve
```

### Compile & run a dev server with live updates
```
npm run serve
```

### Docker use
```
docker build -t vuejs-cookbook/dockerize-vuejs-app .
```
```
docker run -it --network="host" --rm --name dockerize-vuejs-app-1 vuejs-cookbook/dockerize-vuejs-app
```
