import { Container, Button } from "react-bootstrap";

const Hero = () => {
  return (
    <div
      className="text-center text-dark"
      style={{
        background: "linear-gradient(135deg, #ff9a9e, #fad0c4)",
        padding: "120px 20px",
        borderBottomLeftRadius: "50px",
        borderBottomRightRadius: "50px"
      }}
    >
      <Container>
        <h1 className="fw-bold display-4">
          All-in-One PDF Tools
        </h1>
        <h4>Every tool you need to use PDFs, at your fingertips. All are 100% FREE and easy to use!</h4>
        <p className="lead mt-3">
          Merge, Split, Compress, Convert & Secure your PDFs easily
        </p>
        <Button size="lg" className="mt-3">
          Welcome
        </Button>
      </Container>
    </div>
  );
};

export default Hero;