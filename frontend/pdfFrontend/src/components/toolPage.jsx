import React, { useState } from "react";
import axios from "axios";
import { Container, Button, ProgressBar, Spinner, Card } from "react-bootstrap";

export default function ToolPage({ title, endpoint }) {
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);

  const handleSubmit = async () => {
    if (files.length === 0) {
      alert("Please upload file");
      return;
    }

    setLoading(true);
    setProgress(0);
    setResultUrl(null);

    try {
      const res = await uploadFiles(endpoint, files, setProgress);
      window.open(res.fileUrl);
    } catch (err) {
      alert("Error processing file");
    }

    setLoading(false);
  };

  const getAcceptedTypes = () => {
    if (endpoint.includes("pdf-to-word")) return ".pdf";
    if (endpoint.includes("word-to-pdf")) return ".doc,.docx";
    if (endpoint.includes("excel-to-pdf")) return ".xls,.xlsx";
    return "*";
  };

  const getFileName = (endpoint) => {
    if (endpoint.includes("pdf-to-word")) return "converted.docx";
    if (endpoint.includes("word-to-pdf")) return "converted.pdf";
    if (endpoint.includes("excel-to-pdf")) return "converted.pdf";
    return "converted-file";
  };


  const getAcceptedTypes = () => {
  if (endpoint.includes("pdf-to-word")) return ".pdf";
  if (endpoint.includes("word-to-pdf")) return ".doc,.docx";
  if (endpoint.includes("excel-to-pdf")) return ".xls,.xlsx";
  return "*";
};

  const getFileName = (endpoint) => {
    if (endpoint.includes("pdf-to-word")) return "converted.docx";
    if (endpoint.includes("word-to-pdf")) return "converted.pdf";
    if (endpoint.includes("excel-to-pdf")) return "converted.pdf";
    return "converted-file";
  };

  return (
    <Container className="mt-4 text-center">
      <h2>{title}</h2>

      <FileUpload setFiles={setFiles} />

      {loading && <Loader />}
      {loading && <Progress progress={progress} />}

      <Button className="primary-btn mt-3" onClick={handleSubmit}>
        {title}
      </Button>
    </Container>
  );
};

export default ToolPage;