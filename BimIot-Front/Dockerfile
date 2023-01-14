FROM node:19-buster

# make the 'app' folder the current working directory
WORKDIR /vuejs-bimiot

# copy both 'package.json' and 'package-lock.json' (if available)
COPY package*.json ./

# install project dependencies
RUN npm i

# copy project files and folders to the current working directory (i.e. 'app' folder)
COPY . .

RUN npm run preserve

# build app for production with minification
RUN npm run build

EXPOSE 8080
CMD npm run serve