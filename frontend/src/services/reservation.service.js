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
      console.error("Error making a reservation:", error);
      throw error;
    });
};

const getAllReservations = () => {
  return axios.get(apiUrl + "/api/v1/validatedEmployee/getAllReservations", {
    headers: authHeader(),
    method: "GET",
    "Content-Type": "application/json",
  });
};

const deleteReservation = (reservationId) => {
  const deleteData = {
    reservationId: reservationId,
  };
  return axios.delete(apiUrl + "/api/v1/manager/deleteReservation", {
    headers: authHeader(),
    data: deleteData,
    method: "DELETE",
    "Content-Type": "application/json",
  });
};

const reservationsService = {
  addReservation,
  getAllReservations,
  deleteReservation,
};

export default reservationsService;
