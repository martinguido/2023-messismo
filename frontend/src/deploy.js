let apiUrl;
if (process.env.NODE_ENV === "production") {
  apiUrl = process.env.REACT_APP_API_URL_PROD;
} else {
  apiUrl = process.env.REACT_APP_API_URL_DEV;
}

export default apiUrl;
