import { Card } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const ToolCard = ({ title, path }) => {
  const navigate = useNavigate();

  return (
    <Card
      className="card-hover p-3 text-center" 
      onClick={() => navigate(path)}
    >
      <Card.Body>
        <h5>{title}</h5>
      </Card.Body>
    </Card>
  );
};

export default ToolCard;