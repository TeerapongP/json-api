const jsonServer = require('json-server');
const clone = require('clone');
const cors = require("cors");
const data = require('./db.json');

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

const startServer = (server, port) => {
    server.listen(port, () => {
        console.log(`JSON Server is running on port ${port}`);
    });
};

const server = createServer();
startServer(server, port);

module.exports = server;
