import axios from "axios";
import authHeader from "./auth-header";
import apiUrl from "../deploy";

const getBarConfiguration = () => {
  return axios.get(apiUrl + "/api/v1/admin/getBarConfiguration", {
    headers: authHeader(),
    method: "GET",
    "Content-Type": "application/json",
  });
};

const modifyBarCapacity = (modifyBarCapacityDTO) => {
  return axios
    .put(apiUrl + "/api/v1/admin/modifyBarCapacity", modifyBarCapacityDTO, {
      headers: authHeader(),
      method: "PUT",
      "Content-Type": "application/json",
    })
    .then((response) => {
      // console.log("Bar configuration:", response.data);
    })
    .catch((error) => {
      // console.error("Error getting bar configuration:", error);
      throw error;
    });
};

const barService = {
  getBarConfiguration,
  modifyBarCapacity,
};

export default barService;
