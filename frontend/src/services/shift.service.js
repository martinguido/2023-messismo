import axios from "axios";
import authHeader from "./auth-header";
import apiUrl from "../deploy";

const getAllShifts = () => {
  return axios.get(apiUrl + "/api/v1/auth/getAllShifts", {
    headers: authHeader(),
    method: "GET",
    "Content-Type": "application/json",
  });
};

const deleteShift = (shiftNumber) => {
  const newShiftId = {
    shiftId: shiftNumber,
  };
  return axios.delete(apiUrl + "/api/v1/admin/deleteShift", {
    headers: authHeader(),
    data: newShiftId,
    method: "DELETE",
    "Content-Type": "application/json",
  });
};

const addShift = (shift) => {
  return axios
    .post(apiUrl + "/api/v1/admin/addShift", shift, {
      headers: authHeader(),
      method: "POST",
      "Content-Type": "application/json",
    })
    .then((response) => {})
    .catch((error) => {
      console.error("Error al agregar el shift:", error);
      throw error;
    });
};

const getShiftsForADate = (localDate) => {
  return axios
    .post(apiUrl + "/api/v1/auth/getShiftsForADate", localDate, {
      headers: authHeader(),
      method: "POST",
      "Content-Type": "application/json",
    })
    .then((response) => {
      console.log(response);
      return response;
    })
    .catch((error) => {
      console.error("Error al traer los shifts:", error);
      throw error;
    });
};

const shiftService = {
  getAllShifts,
  deleteShift,
  addShift,
  getShiftsForADate,
};

export default shiftService;
