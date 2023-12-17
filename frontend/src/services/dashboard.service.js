import axios from "axios";
import authHeader from "./auth-header";
import apiUrl from "../deploy";

const getDashboard = (data) => {
  return axios.post(apiUrl + "/api/v1/manager/dashboard/getDashboard", data, {
    headers: authHeader(),
  });
};

const getReservationsAndShiftsMetrics = () => {
  return axios.get(
    apiUrl + "/api/v1/manager/dashboard/getReservationsAndShiftsMetrics",
    {
      headers: authHeader(),
      method: "GET",
      "Content-Type": "application/json",
    }
  );
};

const dashboardService = {
  getDashboard,
  getReservationsAndShiftsMetrics,
};

export default dashboardService;
