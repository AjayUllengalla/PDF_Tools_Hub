import { Navbar, Container } from "react-bootstrap";

const NavBar = () => {
  return (
    <Navbar bg="white" className="shadow-sm">
      <Container>
        <Navbar.Brand style={{ fontWeight: "bold", color: "#6c63ff" }}>
          PDF Tools Hub
        </Navbar.Brand>
      </Container>
    </Navbar>
  );
};

export default NavBar;