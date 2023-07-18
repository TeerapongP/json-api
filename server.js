const jsonServer = require('json-server');
const clone = require('clone');
const cors = require("cors");
const data = require('./db.json');
const express = require('express');
const isProductionEnv = process.env.NODE_ENV === 'production';
const port = process.env.PORT || 8000;

const createServer = () => {
    const server = jsonServer.create();
    const router = jsonServer.router(isProductionEnv ? clone(data) : 'db.json', {
        _isFake: isProductionEnv,
    });
    const middlewares = jsonServer.defaults();

    server.use(middlewares);

    server.use((req, res, next) => {
        if (req.path !== '/') {
            router.db.setState(clone(data));
        }
        next();
    });

    server.use(router);

    return server;
};

function searchByTags(keyword) {
    const searchResults = data.filter(trip => trip.tags.includes(keyword));
    return searchResults;
}
const app = express();
app.post('/search', (req, res) => {
    const { keyword } = req.body;
    try {
        const trips = searchByTags(keyword);
        res.json(trips); // Use json method to send the response
    } catch (ex) {
        return res.status(500).json({
            responseCode: -1,
            errMsg: ex.message
        });
    }
});

const startServer = (server, port) => {
    server.listen(port, () => {
        console.log(`JSON Server is running on port ${port}`);
    });
};

const server = createServer();
startServer(server, port);

module.exports = server;
