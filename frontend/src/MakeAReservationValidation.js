function MakeAReservationValidation(values) {
  let error = {};
  const phoneRegex = /^[0-9]*$/;
  const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
  const commentRegex = /^[a-zA-Z0-9\s]{1,150}$/;
  const capacityRegex = /^[1-9]\d*$/;
  if (values.phone === "" && values.email === "") {
    error.emailR = "Please provide an email and/or a phone";
  } else {
    if (!phoneRegex.test(values.phone)) {
      error.phone = "Invalid phone number";
    }
    if (!(emailRegex.test(values.email) || values.email === "")) {
      error.emailR = "Invalid email address";
    }
  }
  if (values.comment === "") {
    error.comment = "Field is empty";
  } else if (!commentRegex.test(values.comment)) {
    error.comment = "Invalid comment";
  }
  if (values.capacity === "") {
    error.capacity = "Field is empty";
  } else if (!capacityRegex.test(values.capacity)) {
    error.capacity = "Invalid capacity";
  }
  if (values.shift === "") {
    error.shift = "Field is empty";
  }
  return error;
}
export default MakeAReservationValidation;
