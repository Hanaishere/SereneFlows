import { useState } from "react";
import axios from "axios";

function App() {
  const [youtubeUrl, setYoutubeUrl] = useState("");
  const [songs, setSongs] = useState([]);

  const handleConvert = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/convert?url=${youtubeUrl}`
      );

      setSongs(response.data);
    } catch (error) {
      console.error(error);
      alert("Error converting playlist");
    }
  };

  return (
    <div style={{ padding: "40px", fontFamily: "Arial" }}>
      <h1>SereneFlows 🎵</h1>

      <input
        type="text"
        placeholder="Paste YouTube playlist link"
        value={youtubeUrl}
        onChange={(e) => setYoutubeUrl(e.target.value)}
        style={{
          width: "400px",
          padding: "10px",
          marginRight: "10px",
        }}
      />

      <button onClick={handleConvert}>Convert</button>

      <div style={{ marginTop: "30px" }}>
        {songs.map((song, index) => (
          <div
            key={index}
            style={{
              border: "1px solid gray",
              padding: "10px",
              marginBottom: "10px",
            }}
          >
            <h3>{song.youtubeSong}</h3>
            <p>{song.artist}</p>

            <a href={song.url} target="_blank">
              Open in Spotify
            </a>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
