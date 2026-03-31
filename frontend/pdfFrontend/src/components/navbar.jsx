import { useState } from "react";
import { useState } from "react";
import { Navbar, Nav, Nav, Container, NavDropdown, NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import { Link } from "react-router-dom";


const LogoSVG = () => (
  <svg
    width="155"
    height="40"
    viewBox="0 0 310 80"
    xmlns="http://www.w3.org/2000/svg"
  >
    <rect x="0" y="0" width="52" height="66" rx="7" fill="white" fillOpacity="0.95" />
    <polygon points="36,0 52,16 36,16" fill="white" fillOpacity="0.45" />
    <rect x="8" y="22" width="22" height="3.5" rx="1.5" fill="#764ba2" opacity="0.9" />
    <rect x="8" y="31" width="30" height="3.5" rx="1.5" fill="#764ba2" opacity="0.7" />
    <rect x="8" y="40" width="19" height="3.5" rx="1.5" fill="#764ba2" opacity="0.6" />
    <rect x="8" y="49" width="26" height="3.5" rx="1.5" fill="#764ba2" opacity="0.45" />
    <circle cx="44" cy="57" r="13" fill="#ff6b6b" />
    <rect x="41" y="44.5" width="6" height="5" rx="1" fill="#ff6b6b" stroke="white" strokeWidth="1.5" />
    <rect x="41" y="64.5" width="6" height="5" rx="1" fill="#ff6b6b" stroke="white" strokeWidth="1.5" />
    <rect x="32"  y="54"  width="5" height="6" rx="1" fill="#ff6b6b" stroke="white" strokeWidth="1.5" />
    <rect x="57"  y="54"  width="5" height="6" rx="1" fill="#ff6b6b" stroke="white" strokeWidth="1.5" />
    <circle cx="44" cy="57" r="4.5" fill="white" />
    <circle cx="44" cy="57" r="2"   fill="#ff6b6b" />
    <text
      x="65" y="30"
      fontSize="21" fontWeight="700"
      fill="white"
      fontFamily="'Segoe UI', Arial, sans-serif"
      letterSpacing="-0.3"
    >PDF</text>
    <text
      x="65" y="52"
      fontSize="21" fontWeight="700"
      fill="white" fillOpacity="0.82"
      fontFamily="'Segoe UI', Arial, sans-serif"
      letterSpacing="-0.3"
    >Tools</text>
    <rect x="153" y="8" width="2" height="50" rx="1" fill="white" opacity="0.25" />
    <text
      x="163" y="48"
      fontSize="30" fontWeight="800"
      fill="#ffdd57"
      fontFamily="'Segoe UI', Arial, sans-serif"
      letterSpacing="-1"
    >Hub</text>
  </svg>
);

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
