import axios from "axios";
import authHeader from "./auth-header";
import apiUrl from "../deploy";

const addReservation = (newReservationData) => {
  return axios
    .post(apiUrl + "/api/v1/auth/addReservation", newReservationData, {
      headers: authHeader(),
      method: "POST",
      "Content-Type": "application/json",
    })
    .then((response) => {
      console.log("Reservation added successfully:", response.data);
    })
    .catch((error) => {
      console.log(newReservationData);
      console.error("Error making a reservation:", error);
      throw error;
    });
};

const reservationService = {
  addReservation,
};

export default reservationService;
