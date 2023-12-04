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
    .then((response) => {
      console.log("Shift agregado con éxito:", response.data);
    })
    .catch((error) => {
      console.error("Error al agregar el shift:", error);
      throw error;
    });
};
const shiftService = {
  getAllShifts,
  deleteShift,
  addShift,
};

export default shiftService;
