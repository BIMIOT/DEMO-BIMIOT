// vue.config.js
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const webpack = require('webpack');

module.exports = {
  publicPath: './',
  transpileDependencies: [
    'vuetify'
  ]
  
}