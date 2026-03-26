import { ProgressBar } from "react-bootstrap";

const Progress = ({ progress }) => {
  return <ProgressBar now={progress} label={`${progress}%`} />;
};

export default Progress;