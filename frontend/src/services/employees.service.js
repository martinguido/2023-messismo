import axios from "axios";
import authHeader from "./auth-header";
import apiUrl from "../deploy";

const getAllEmployees = () => {
  return axios.get(apiUrl + "/api/v1/manager/getAllEmployees", {
    headers: authHeader(),
    method: "GET",
    "Content-Type": "application/json",
  });
};

const validateEmployee = (employeeId) => {
  const data = {
    userId: employeeId,
  };
  return axios
    .put(apiUrl + "/api/v1/manager/validateEmployee", data, {
      headers: authHeader(),
      "Content-Type": "application/json",
    })
    .then((response) => {})
    .catch((error) => {
      console.error("Error al validar al empleado:", error);
    });
};

const validateAdmin = (employeeId) => {
  const data = {
    userId: employeeId,
  };
  return axios
    .put(apiUrl + `/api/v1/admin/validateAdmin`, data, {
      headers: authHeader(),
      "Content-Type": "application/json",
    })
    .then((response) => {})
    .catch((error) => {
      console.error("Error al validar al administrador:", error);
    });
};

const employeeService = {
  getAllEmployees,
  validateEmployee,
  validateAdmin,
};

export default employeeService;
