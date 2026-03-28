import { Container, Button } from "react-bootstrap";

const Hero = () => {
  return (
    <div className="hero-section text-center">
      <Container>
        <h1>All-in-One PDF Tools</h1>
        <h4>Every tool you need to use PDFs, at your fingertips. All are 100% FREE and easy to use! </h4>
        <p>Merge, Split, Compress, Convert & Secure your PDFs easily</p>
        <Button variant="primary" size="lg">Welcome</Button>
      </Container>
    </div>
  );
};

export default Hero;