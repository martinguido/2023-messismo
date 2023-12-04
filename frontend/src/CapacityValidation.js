function CapacityValidation(values) {
  let error = {};
  const capacityRegex = /^[1-9]\d*$/;
  if (values.capacity === "") {
    error.capacity = "Field is empty";
  } else if (!capacityRegex.test(values.capacity)) {
    error.capacity = "Invalid capacity";
  }
  return error;
}
export default CapacityValidation;
