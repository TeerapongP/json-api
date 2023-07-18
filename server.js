const jsonServer = require('json-server');
const clone = require('clone');
const cors = require('cors');
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

    server.use(cors());
    server.use(middlewares);
    server.use(express.json()); // Parse JSON request bodies

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

// Search endpoint with GET request and query parameter
app.get('/search', (req, res) => {
    const { keyword } = req.query;
    console.log('Received keyword:', keyword); // Optional: Log the received keyword in the server console
    try {
        const trips = searchByTags(keyword);
        res.json(trips);
    } catch (ex) {
        return res.status(500).json({
            responseCode: -1,
            errMsg: ex.message,
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
