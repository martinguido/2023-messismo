function RecoverPasswordValidation(values) {
  let error = {};
  const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  if (values.email === "") {
    error.email = "Field is empty";
  } else if (!emailRegex.test(values.email)) {
    error.email = "Invalid email address";
  }

  return error;
}
export default RecoverPasswordValidation;
