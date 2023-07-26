const express = require('express');
const cors = require('cors');
const data = require('./Json/characters'); // Assuming the JSON data is stored in a file called 'db.json'

const app = express();
const port = process.env.PORT || 8000;


app.use(cors());
app.use(express.json()); // Parse JSON request bodies

// Search endpoint with GET request and query parameter
function searchByHouse(keyword) {
    const searchResults = data.filter((character) => {
        return character.house.toLowerCase() === keyword.toLowerCase();
    });

    return searchResults;
}
app.post('/search', (req, res) => {
    const { keyword } = req.query;
    if (!keyword) {
        return res.status(400).json({ error: 'Missing "keyword" in the request body' });
    }

    try {
        const house = searchByHouse(keyword);
        res.json(house);
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
