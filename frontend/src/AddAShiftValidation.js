function AddAShiftValidation(values) {
  let error = {};
  if (isNaN(values.startingTime)) {
    error.startingTime = "Field is incomplete";
  }
  if (values.startingTime === "") {
    error.startingTime = "Field is empty";
  }
  if (isNaN(values.finishingTime)) {
    error.finishingTime = "Field is incomplete";
  }
  if (values.finishingTime === "") {
    error.finishingTime = "Field is empty";
  }

  return error;
}

export default AddAShiftValidation;
