import axios from "axios";
import authHeader from "./auth-header";
import apiUrl from "../deploy";

const getAllCategories = () => {
  return axios.get(apiUrl + "/api/v1/validatedEmployee/getAllCategories", {
    headers: authHeader(),
    method: "GET",
    "Content-Type": "application/json",
  });
};

const addCategory = (categoryName) => {
  return axios
    .post(
      apiUrl + "/api/v1/manager/category/addCategory",
      { categoryName: categoryName },
      {
        headers: authHeader(),
        method: "POST",
        "Content-Type": "application/json",
      }
    )
    .then((response) => {})
    .catch((error) => {
      console.error("Error al agregar la categoria:", error);
      throw error;
    });
};

const deleteCategory = (categoryName) => {
  const data = {
    categoryName: categoryName,
  };
  return axios
    .delete(apiUrl + "/api/v1/manager/category/deleteCategory", {
      data: data,
      headers: authHeader(),
      "Content-Type": "application/json",
    })
    .then((response) => {})
    .catch((error) => {
      console.error("Error al eliminar la categoria:", error);
      throw error;
    });
};

const categoryService = {
  getAllCategories,
  addCategory,
  deleteCategory,
};

export default categoryService;
