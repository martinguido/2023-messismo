import React from "react";
import Navbar from "../components/Navbar";
import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";
import ReservationsList from "../components/ReservationsList";
import { Container, MainContent } from "./ScreenStyles";

function Reservations() {
  const { user: currentUser } = useSelector((state) => state.auth);
  const clicked = useSelector((state) => state.navigation.clicked);

  const isAdminOrManager =
    currentUser &&
    (currentUser.role === "MANAGER" ||
      currentUser.role === "ADMIN" ||
      currentUser.role === "VALIDATEDEMPLOYEE");
  const contentVisible = !clicked;

  if (!currentUser) {
    return <Navigate to="/" />;
  }
  if (!isAdminOrManager) {
    return <Navigate to="/homepage" />;
  }

  return (
    <Container className="products">
      <Navbar />
      <MainContent visible={contentVisible}>
        <ReservationsList />
      </MainContent>
    </Container>
  );
}

export default Reservations;
