import axios from "axios";
import apiUrl from "../deploy";

const API_URL = apiUrl + "/api/v1/auth";

const register = (username, email, password) => {
  return axios.post(API_URL + "/register", {
    username,
    email,
    password,
  });
};

const login = (email, password) => {
  return axios
    .post(API_URL + "/authenticate", {
      email,
      password,
    })
    .then((response) => {
      if (response.data.access_token && response.data.role !== "EMPLOYEE") {
        localStorage.setItem("user", JSON.stringify(response.data));
      }

      return response.data;
    });
};

const logout = () => {
  localStorage.removeItem("user");
};

const forgotPassword = (email) => {
  return axios
    .post(API_URL + "/forgotPassword", email, {
      headers: {
        "Content-Type": "text/plain",
      },
    })
    .then((response) => {
      return response.data;
    })
    .catch((error) => {
      throw error;
    });
};
const changePassword = (form) => {
  return axios
    .post(API_URL + "/changeForgottenPassword", form, {
      headers: {
        "Content-Type": "application/json",
      },
    })
    .then((response) => {
      return response.data;
    })
    .catch((error) => {
      throw error;
    });
};

const authService = {
  register,
  login,
  logout,
  forgotPassword,
  changePassword,
};

export default authService;
