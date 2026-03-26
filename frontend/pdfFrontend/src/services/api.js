import axios from "axios";

const API = "http://localhost:5000/api";

export const uploadFiles = async (endpoint, files, setProgress) => {
  const formData = new FormData();

  files.forEach(file => formData.append("files", file));

  const res = await axios.post(`${API}/${endpoint}`, formData, {
    onUploadProgress: (data) => {
      const percent = Math.round((data.loaded * 100) / data.total);
      setProgress(percent);
    }
  });

  return res.data;
};