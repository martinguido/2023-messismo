function CapacityValidation(values) {
  let error = {};
  if (values.capacity <= 0) {
    error.capacity = "New capacity must be greater than 0";
  }
  if (values.capacity === "") {
    error.capacity = "Field is empty";
  }
  return error;
}
export default CapacityValidation;
