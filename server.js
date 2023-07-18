const express = require('express');
const cors = require('cors');
const data = require('./Json/db.json'); // Assuming the JSON data is stored in a file called 'db.json'

const app = express();
const port = process.env.PORT || 8000;

function searchByTags(keyword) {
    const searchResults = data.trips.filter(trip => trip.tags.includes(keyword));
    return searchResults;
}
app.use(cors());
app.use(express.json()); // Parse JSON request bodies

// Search endpoint with GET request and query parameter
app.post('/search', (req, res) => {
    const { keyword } = req.body;
    if (!keyword) {
        return res.status(400).json({ error: 'Missing "keyword" in the request body' });
    }

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
app.get('/', (req, res) => {
    res.json(data);
});
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
