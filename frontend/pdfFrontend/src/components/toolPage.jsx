import React, { useState } from "react";
import axios from "axios";
import { Container, Button, ProgressBar, Spinner, Card } from "react-bootstrap";

export default function ToolPage({ title, endpoint }) {
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [resultUrl, setResultUrl] = useState(null);


  const handleFileChange = (e) => {
    setFiles(Array.from(e.target.files));
  };

  const handleSubmit = async () => {
    if (files.length === 0) {
      alert("Please upload a file first");
      return;
    }

    setLoading(true);
    setProgress(0);
    setResultUrl(null);

    try {
      const formData = new FormData();

      files.forEach((file) => {
        formData.append("file", file); 
      });

      const response = await axios.post(
        `http://localhost:8080${endpoint}`,
        formData,
        {
          responseType: "blob", 
          onUploadProgress: (e) => {
            const percent = Math.round((e.loaded * 100) / e.total);
            setProgress(percent);
          },
        }
      );

      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);

      setResultUrl(url);

    } catch (error) {
      console.error(error);
      alert("Error processing file");
    }

    setLoading(false);
  };

  return (
    <Container className="mt-5">
      <Card className="p-4 shadow-lg text-center">
        <h3 className="mb-3">{title}</h3>

        <input
          type="file"
          className="form-control"
          multiple
          onChange={handleFileChange}
        />

        <Button
          className="mt-3"
          variant="primary"
          onClick={handleSubmit}
          disabled={loading}
        >
          {loading ? "Processing..." : "Convert"}
        </Button>

        {loading && (
          <div className="mt-3">
            <Spinner animation="border" />
          </div>
        )}

        {loading && (
          <ProgressBar
            now={progress}
            label={`${progress}%`}
            className="mt-3"
          />
        )}

        {resultUrl && (
          <div className="mt-4">
            <a
              href={resultUrl}
              download="converted-file"
              className="btn btn-success"
            >
              Download File
            </a>
          </div>
        )}

       
        {resultUrl && (
          <div className="mt-4">
            <iframe
              src={resultUrl}
              width="100%"
              height="500px"
              title="Preview"
              style={{ border: "1px solid #ccc" }}
            />
          </div>
        )}
      </Card>
    </Container>
  );
}