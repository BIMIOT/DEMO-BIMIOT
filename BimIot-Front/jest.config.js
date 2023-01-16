module.exports = {
    verbose: true,
    roots: ["<rootDir>/src/", "<rootDir>/test/specs/"],
    moduleFileExtensions: ['js', 'vue'],
    moduleNameMapper: {
        '^@/(.*)$': '<rootDir>/src/$1',
    },
    transform: {
        "^.+\\.js$": "babel-jest",
        ".*\\.(vue)$": "<rootDir>/node_modules/vue-jest"
    },
    snapshotSerializers: [
        "<rootDir>/node_modules/jest-serializer-vue"
    ]
}