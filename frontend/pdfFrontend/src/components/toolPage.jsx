import { useState } from "react";
import { Container, Button } from "react-bootstrap";
import FileUpload from "./fileupload";
import Loader from "./loader";
import Progress from "./progressbar";
import { uploadFiles } from "../services/api";

const ToolPage = ({ title, endpoint }) => {
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);

  const handleSubmit = async () => {
    if (files.length === 0) {
      alert("Please upload file");
      return;
    }

    setLoading(true);

    try {
      const res = await uploadFiles(endpoint, files, setProgress);
      window.open(res.fileUrl);
    } catch (err) {
      alert("Error processing file");
    }

    setLoading(false);
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