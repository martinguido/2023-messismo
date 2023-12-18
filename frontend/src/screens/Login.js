import React, { useState, useEffect, forwardRef } from "react";
import "./Login.css";
import "../App.css";
import image from "../images/signin2.png";
import logo from "../images/logo.png";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { BsFillShieldLockFill } from "react-icons/bs";
import { MdEmail } from "react-icons/md";
import styled from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import SInPopUp from "../components/SignInPopUp";
import signinvalidation from "../SignInValidation";
import { login } from "../redux/auth";
import { clearMessage } from "../redux/message";
import Stack from "@mui/material/Stack";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";
import Alert from "@mui/material/Alert";
import TextField from "@mui/material/TextField";
import authService from "../services/auth.service";
import RecoverPasswordValidation from "../RecoverPasswordValidation";
import ChangePasswordValidation from "../ChangePasswordValidation";
import MakeAReservationValidation from "../MakeAReservationValidation";
import DialogContent from "@mui/material/DialogContent";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import MenuItem from "@mui/material/MenuItem";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import shiftService from "../services/shift.service";
import reservationService from "../services/reservation.service";
import Select from "@mui/material/Select";
import { CircularProgress } from "@mui/material";
import Box from "@mui/material/Box";
import dayjs from "dayjs";
const ForgotPassword = styled.a`
  text-decoration: none;
  color: #a7d0cd;
`;

const ErrorMessage = styled.h4`
  color: red;
  font-family: "Roboto";
  width: 240px;
  word-wrap: break-word;
`;

const Alert2 = forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const tomorrow = dayjs().add(1, "day");
function Login() {
  const [open, setOpen] = useState(false);

  const [isRegistered, setIsRegistered] = useState(false);

  const [isSignInValid, setIsSignInValid] = useState(false);
  const [signinvalues, setSignInValues] = useState({
    email: "",
    password: "",
  });
  const [signinerrors, setSignInErrors] = useState({});
  const [SignInPopUp, setSignInPopUp] = useState(false);

  const { isLoggedIn } = useSelector((state) => state.auth);
  // const { message } = useSelector((state) => state.message);
  const { user: currentUser } = useSelector((state) => state.auth);

  let navigate = useNavigate();
  const dispatch = useDispatch();
  const [errors, setErrors] = useState({});
  const [openForm, setOpenForm] = useState(false);
  const [email, setEmail] = useState("");
  const [pin, setPin] = useState("");
  const [password, setPassword] = useState("");
  const [openChangePasswordForm, setOpenChangePasswordForm] = useState(false);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertText, setAlertText] = useState("");
  const [isOperationSuccessful, setIsOperationSuccessful] = useState(false);
  const [repeatPassword, setRepeatPassword] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedShift, setSelectedShift] = useState("");
  const [shifts, setShifts] = useState([]);
  const [emailR, setEmailR] = useState("");
  const [phone, setPhone] = useState("");
  const [comment, setComment] = useState("");
  const [capacity, setCapacity] = useState("");
  const [openReservationForm, setOpenReservationForm] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingReservation, setIsLoadingReservation] = useState(false);
  const [isLoadingMail, setIsLoadingMail] = useState(false);
  const [isLoadingPin, setIsLoadingPin] = useState(false);
  const [isLoadingShift, setIsLoadingShift] = useState(false);
  useEffect(() => {
    if (selectedDate !== "") {
      setIsLoadingShift(true);
      const fechaDate = new Date(selectedDate);
      const pruebaDate = new Date(
        fechaDate.getFullYear(),
        fechaDate.getMonth(),
        fechaDate.getDate()
      );
      const fechaISO = pruebaDate.toISOString();
      const indiceT = fechaISO.indexOf("T");
      const fechaSinHora = fechaISO.substring(0, indiceT);
      const localDate = { localDate: fechaSinHora };
      shiftService
        .getShiftsForADate(localDate)
        .then((response) => {
          console.log(response.data);
          setShifts(response.data);
          setIsLoadingShift(false);
        })
        .catch((error) => {
          console.error("Error al obtener turnos:", error);
        })
        .finally(() => {
          setIsLoadingShift(false);
        });
    }
  }, [selectedDate]);

  const handleDateChange = (date) => {
    setSelectedDate(date);
  };
  const handleMakeAReservationOpenForm = () => {
    setOpenReservationForm(true);
  };

  const handleMakeAReservationCloseForm = () => {
    setCapacity("");
    setSelectedShift("");
    setShifts([]);
    setErrors({});
    setSelectedDate("");
    setComment("");
    setEmailR("");
    setPhone("");
    setOpenReservationForm(false);
  };
  const handleInputEmailRChange = (e) => {
    setEmailR(e.target.value);
  };
  const handleInputPhoneChange = (event) => {
    let inputValue = event.target.value;
    inputValue = inputValue.replace(/[^0-9]/g, "");
    event.target.value = inputValue;
    setPhone(inputValue);
  };
  const handleInputCapacityChange = (event) => {
    let inputValue = event.target.value;
    inputValue = inputValue.replace(/[^0-9]/g, "");
    event.target.value = inputValue;
    setCapacity(inputValue);
  };

  const handleInputCommentChange = (e) => {
    setComment(e.target.value);
  };

  const handleShiftChange = (event) => {
    setSelectedShift(event.target.value);
  };

  const handleMakeAReservation = () => {
    const validationErrors = MakeAReservationValidation({
      phone: phone,
      email: emailR,
      comment: comment,
      capacity: capacity,
      shift: selectedShift,
    });

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      console.log(validationErrors);
    } else {
      setIsLoadingReservation(true);
      const selectedShiftObj = shifts.find(
        (shi) => shi.shiftId === selectedShift
      );
      const fechaDate = new Date(selectedDate);
      const fechaISO = fechaDate.toISOString();
      const indiceT = fechaISO.indexOf("T");
      const fechaSinHora = fechaISO.substring(0, indiceT);
      const newReservationData = {
        capacity: capacity,
        shift: selectedShiftObj,
        reservationDate: fechaSinHora,
        comment: comment,
        clientEmail: emailR,
        clientPhone: phone,
      };
      reservationService
        .addReservation(newReservationData)
        .then((response) => {
          setAlertText("Reservation made succesfully!");
          setIsOperationSuccessful(true);
          setOpenSnackbar(true);
          handleMakeAReservationCloseForm();
          setErrors({});
        })
        .catch((error) => {
          setAlertText(error.response.data);
          setIsOperationSuccessful(false);
          setOpenSnackbar(true);
        })
        .finally(() => {
          setCapacity("");
          setSelectedShift("");
          setShifts([]);
          setErrors({});
          setSelectedDate("");
          setComment("");
          setEmailR("");
          setPhone("");
          setIsLoadingReservation(false);
        });
    }
  };

  useEffect(() => {
    const validationErrors = signinvalidation(signinvalues);
    setSignInErrors(validationErrors);
    setIsSignInValid(Object.keys(validationErrors).length === 0);
  }, [signinvalues]);

  useEffect(() => {
    dispatch(clearMessage());
  }, [dispatch]);

  const handleLogin = (userData) => {
    setIsLoading(true);
    const email = userData.email;
    const password = userData.password;

    dispatch(login({ email, password }))
      .unwrap()
      .then((response) => {
        const userRole = response.user.role;
        if (
          userRole === "ADMIN" ||
          userRole === "MANAGER" ||
          userRole === "VALIDATEDEMPLOYEE"
        ) {
          navigate("/homepage");
        }
        if (userRole === "EMPLOYEE") {
          setIsRegistered(true);
          setSignInPopUp(true);
        }
      })
      .catch(() => {
        setIsRegistered(false);
        setSignInPopUp(true);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  if (
    isLoggedIn &&
    (currentUser.role === "ADMIN" ||
      currentUser.role === "MANAGER" ||
      currentUser.role === "VALIDATEDEMPLOYEE")
  ) {
    return <Navigate to="/homepage" />;
  }

  const handleSignInInput = (e) => {
    setSignInValues({ ...signinvalues, [e.target.name]: e.target.value });
  };

  const handleSignInErrors = (e) => {
    setSignInErrors((prevErrors) => ({
      ...prevErrors,
      [e.target.name]: "",
    }));
  };

  function handleSignInValidation() {
    const validationErrors = signinvalidation(signinvalues);
    setSignInErrors(validationErrors);
    if (Object.keys(validationErrors).length === 0) {
      setIsSignInValid(true);
    } else {
      setIsSignInValid(false);
      handleSnackClick();
    }
  }

  const handleSnackClick = () => {
    setOpen(true);
  };

  const handleSnackClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setOpen(false);
  };

  const handleOpenForm = () => {
    setOpenForm(true);
  };
  const handleCloseForm = () => {
    setOpenForm(false);
  };

  const handleInputChange = (e) => {
    setEmail(e.target.value);
  };

  const handleSendEmail = () => {
    const validationErrors = RecoverPasswordValidation({
      email,
    });

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
    } else {
      setIsLoadingMail(true);
      authService
        .forgotPassword(email)
        .then((response) => {
          setAlertText("Email sent succesfully!");
          setIsOperationSuccessful(true);
          setOpenSnackbar(true);
          handleCloseForm();
          setOpenChangePasswordForm(true);
        })
        .catch((error) => {
          setAlertText("Error sending password recovery email");
          setIsOperationSuccessful(false);
          setOpenSnackbar(true);
        })
        .finally(() => {
          setEmail("");
          setIsLoadingMail(false);
        });
    }
  };

  const handleCloseChangePasswordForm = () => {
    setOpenChangePasswordForm(false);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const handleRepeatPasswordChange = (e) => {
    setRepeatPassword(e.target.value);
  };

  const handlePinChange = (e) => {
    setPin(e.target.value);
  };

  const handleChangePassword = () => {
    const form = {
      email: email,
      newPassword: password,
      pin: pin,
    };

    const validationErrors = ChangePasswordValidation({
      email,
      password,
      pin,
      repeatPassword,
    });

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
    } else {
      setIsLoadingPin(true);
      authService
        .changePassword(form)
        .then((response) => {
          setAlertText("Password changed succesfully!");
          setIsOperationSuccessful(true);
          setOpenSnackbar(true);
          setOpenChangePasswordForm(false);
        })
        .catch((error) => {
          setAlertText("Error changing password");
          setIsOperationSuccessful(false);
          setOpenSnackbar(true);
        })
        .finally(() => {
          setEmail("");
          setPassword("");
          setPin("");
          setRepeatPassword("");
          setIsLoadingPin(false);
        });
    }
  };

  const handleChange = (e) => {
    e.preventDefault();
  };

  return (
    <div>
      <div className="loginPage flx">
        <div className="cntainr flx">
          <div className="formDiv flx">
            <form action="" className="form grd">
              <div className="headerDiv">
                <img src={logo} className="logoimg" alt="logo"></img>
              </div>

              <div className="inputDiv">
                <label htmlFor="email" className="labl">
                  Email
                </label>
                <div className="inpt flx">
                  <MdEmail className="icn" />
                  <input
                    type="email"
                    name="email"
                    id="emailSiId"
                    placeholder="Enter your email"
                    className="inpt"
                    onChange={handleSignInInput}
                    onBlur={handleSignInErrors}
                  />
                </div>
                {signinerrors.email && (
                  <ErrorMessage>{signinerrors.email}</ErrorMessage>
                )}
              </div>

              <div className="inputDiv">
                <label htmlFor="password" className="labl">
                  Password
                </label>
                <div className="inpt flx">
                  <BsFillShieldLockFill className="icn" />
                  <input
                    type="password"
                    name="password"
                    id="passwordSiId"
                    placeholder="Enter your password"
                    className="inpt"
                    onChange={handleSignInInput}
                    onBlur={handleSignInErrors}
                  />
                </div>
                {signinerrors.password && (
                  <ErrorMessage>{signinerrors.password}</ErrorMessage>
                )}
              </div>

              {isLoading && (
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    marginTop: "10%",
                  }}
                >
                  <CircularProgress style={{ color: "#a4d4cc" }} />
                </Box>
              )}

              <Link
                className="btn flx"
                onClick={() => {
                  handleSignInValidation();
                  if (isSignInValid) {
                    const userData = {
                      email: signinvalues.email,
                      password: signinvalues.password,
                    };
                    handleLogin(userData);
                  }
                }}
                disabled={
                  Object.keys(signinerrors).length > 0 || !isSignInValid
                }
              >
                <span>Sign In</span>
              </Link>

              <span className="forgotPassword">
                <ForgotPassword
                  onClick={(e) => {
                    e.preventDefault();
                    handleOpenForm();
                  }}
                >
                  Forgot your Password?
                </ForgotPassword>
              </span>
              <Link
                className="btn flx"
                onClick={(e) => {
                  e.preventDefault();
                  handleMakeAReservationOpenForm();
                }}
              >
                <span>Make a Reservation</span>
              </Link>
              <Dialog
                open={openReservationForm}
                // dividers={true}
                onClose={handleMakeAReservationCloseForm}
                aria-labelledby="form-dialog-title"
                className="custom-dialog"
                maxWidth="sm"
                fullWidth
              >
                <DialogContent>
                  <div>
                    <h1 style={{ marginBottom: "3%", fontSize: "1.6rem" }}>
                      Make a Reservation
                    </h1>
                    <hr
                      style={{
                        borderTop: "1px solid grey",
                        marginBottom: "3%",
                        width: "100%",
                      }}
                    />
                    <p>Please enter you email</p>
                    <TextField
                      required
                      id="name"
                      value={emailR}
                      onChange={handleInputEmailRChange}
                      variant="outlined"
                      error={errors.emailR ? true : false}
                      helperText={errors.emailR || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.1rem",
                        },
                      }}
                    />
                    <p>Please enter you phone number</p>
                    <TextField
                      required
                      id="name"
                      value={phone}
                      onChange={handleInputPhoneChange}
                      variant="outlined"
                      error={errors.phone ? true : false}
                      helperText={errors.phone || ""}
                      inputProps={{
                        pattern: "[0-9]*",
                        inputMode: "numeric",
                      }}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.1rem",
                        },
                      }}
                    />

                    <p>Please enter a capacity *</p>
                    <TextField
                      required
                      id="name"
                      value={capacity}
                      onChange={handleInputCapacityChange}
                      variant="outlined"
                      error={errors.capacity ? true : false}
                      inputProps={{
                        pattern: "[0-9]*",
                        inputMode: "numeric",
                      }}
                      helperText={errors.capacity || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.0rem",
                        },
                      }}
                    />
                    {/* <TextField
              required
              id="name"
              value={capacity}
              onChange={handleInputCapacityChange}
              variant="outlined"
              error={errors.capacity ? true : false}
              helperText={errors.capacity || ""}
              style={{
                width: "80%",
                marginTop: "3%",
                fontSize: "1.1rem",
              }}
              InputProps={{
                style: {
                  fontSize: "1rem",
                },
              }}
              FormHelperTextProps={{
                style: {
                  fontSize: "1.1rem",
                },
              }}
            /> */}

                    <p style={{ marginTop: "3.5%", marginBottom: "3.5%" }}>
                      Please enter a date
                    </p>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <DatePicker
                        minDate={tomorrow}
                        // disablePast
                        variant="outlined"
                        error={errors.date ? true : false}
                        // helperText={errors.date || ""}
                        label={"YYYY-MM-DD"}
                        views={["year", "month", "day"]}
                        format="YYYY-MM-DD"
                        value={selectedDate}
                        onChange={handleDateChange}
                      />
                    </LocalizationProvider>
                    {isLoadingShift ? (
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "center",
                          alignItems: "center",
                          marginTop: "10%",
                        }}
                      >
                        <CircularProgress style={{ color: "#a4d4cc" }} />
                      </Box>
                    ) : (
                      <>
                        <p style={{ marginTop: "3.5%" }}>
                          Please enter a shift *
                        </p>
                        <Select
                          labelId="demo-simple-select-label"
                          id="demo-simple-select"
                          value={selectedShift}
                          onChange={handleShiftChange}
                          error={errors.shift ? true : false}
                          // helperText={errors.shift || ""}
                          style={{
                            width: "80%",
                            marginTop: "3%",
                            marginBottom: "3%",
                            fontSize: "1.1rem",
                          }}
                        >
                          {shifts.length === 0 ? (
                            <MenuItem
                              style={{
                                color: "black",
                              }}
                              key="no-data"
                              value="no-data"
                              disabled
                            >
                              No shifts available for selected date
                            </MenuItem>
                          ) : (
                            shifts.map((shift) => (
                              <MenuItem
                                style={{
                                  color: "black",
                                }}
                                key={shift.shiftId}
                                value={shift.shiftId}
                              >
                                {shift.startingHour.substring(0, 5) +
                                  " - " +
                                  shift.finishingHour.substring(0, 5)}
                              </MenuItem>
                            ))
                          )}
                        </Select>
                      </>
                    )}

                    <p style={{ marginBottom: "3.5%" }}>
                      Please enter a comment *
                    </p>
                    <TextField
                      required
                      id="name"
                      value={comment}
                      onChange={handleInputCommentChange}
                      variant="outlined"
                      error={errors.comment ? true : false}
                      helperText={errors.comment || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.1rem",
                        },
                      }}
                    />
                    {isLoadingReservation && (
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "center",
                          alignItems: "center",
                          marginTop: "10%",
                        }}
                      >
                        <CircularProgress style={{ color: "#a4d4cc" }} />
                      </Box>
                    )}
                    <div
                      className="buttons"
                      style={{
                        flex: 1,
                        display: "flex",
                        flexDirection: "row",
                        justifyContent: "flex-end",
                      }}
                    >
                      <Button
                        variant="outlined"
                        style={{
                          color: "grey",
                          borderColor: "grey",
                          fontSize: "1rem",
                        }}
                        onClick={handleMakeAReservationCloseForm}
                      >
                        Cancel
                      </Button>
                      <Button
                        variant="contained"
                        style={{
                          marginLeft: "3%",
                          fontSize: "1rem",
                          backgroundColor: "#a4d4cc",
                          color: "black",
                        }}
                        onClick={handleMakeAReservation}
                      >
                        Reserve
                      </Button>
                    </div>
                  </div>
                </DialogContent>
              </Dialog>
              {/* <Dialog
                open={openReservationForm}
                // dividers={true}
                onClose={handleMakeAReservationCloseForm}
                aria-labelledby="form-dialog-title"
                className="custom-dialog"
                maxWidth="sm"
                fullWidth
              >
                <DialogContent>
                  <div>
                    <h1 style={{ marginBottom: "3%", fontSize: "1.6rem" }}>
                      Make a Reservation
                    </h1>
                    <hr
                      style={{
                        borderTop: "1px solid grey",
                        marginBottom: "3%",
                        width: "100%",
                      }}
                    />
                    <p>Please enter you email</p>
                    <TextField
                      required
                      id="name"
                      value={emailR}
                      onChange={handleInputEmailRChange}
                      variant="outlined"
                      error={errors.emailR ? true : false}
                      helperText={errors.emailR || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.1rem",
                        },
                      }}
                    />
                    <p>Please enter you phone number</p>
                    <TextField
                      required
                      id="name"
                      value={phone}
                      onChange={handleInputPhoneChange}
                      variant="outlined"
                      error={errors.phone ? true : false}
                      helperText={errors.phone || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.1rem",
                        },
                      }}
                    />

                    <p>Please enter a capacity *</p>
                    <TextField
                      required
                      id="name"
                      value={capacity}
                      onChange={handleInputCapacityChange}
                      variant="outlined"
                      error={errors.capacity ? true : false}
                      helperText={errors.capacity || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.1rem",
                        },
                      }}
                    />
                    <p>Please enter a shift *</p>

                    <Select
                      labelId="demo-simple-select-label"
                      id="demo-simple-select"
                      value={selectedShift}
                      onChange={handleShiftChange}
                      error={errors.shift ? true : false}
                      helperText={errors.shift || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                    >
                      {shifts.map((shift) => (
                        <MenuItem
                          style={{
                            color: "black",
                          }}
                          key={shift.shiftId}
                          value={shift.shiftId}
                        >
                          {shift.startingHour.substring(0, 5) +
                            " - " +
                            shift.finishingHour.substring(0, 5)}
                        </MenuItem>
                      ))}
                    </Select>

                    <p style={{ marginBottom: "3.5%" }}>Please enter a date</p>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <DatePicker
                        disablePast
                        variant="outlined"
                        error={errors.date ? true : false}
                        helperText={errors.date || ""}
                        label={"YYYY-MM-DD"}
                        views={["year", "month", "day"]}
                        format="YYYY-MM-DD"
                        value={selectedDate}
                        onChange={handleDateChange}
                      />
                    </LocalizationProvider>
                    <p style={{ marginTop: "3.5%" }}>
                      Please enter a comment *
                    </p>
                    <TextField
                      required
                      id="name"
                      value={comment}
                      onChange={handleInputCommentChange}
                      variant="outlined"
                      error={errors.comment ? true : false}
                      helperText={errors.comment || ""}
                      style={{
                        width: "80%",
                        marginTop: "3%",
                        marginBottom: "3%",
                        fontSize: "1.1rem",
                      }}
                      InputProps={{
                        style: {
                          fontSize: "1rem",
                        },
                      }}
                      FormHelperTextProps={{
                        style: {
                          fontSize: "1.1rem",
                        },
                      }}
                    />
                    {isLoadingReservation && (
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "center",
                          alignItems: "center",
                          marginTop: "10%",
                        }}
                      >
                        <CircularProgress style={{ color: "#a4d4cc" }} />
                      </Box>
                    )}
                    <div
                      className="buttons"
                      style={{
                        flex: 1,
                        display: "flex",
                        flexDirection: "row",
                        justifyContent: "flex-end",
                      }}
                    >
                      <Button
                        variant="outlined"
                        style={{
                          color: "grey",
                          borderColor: "grey",
                          fontSize: "1rem",
                        }}
                        onClick={handleMakeAReservationCloseForm}
                      >
                        Cancel
                      </Button>
                      <Button
                        variant="contained"
                        style={{
                          marginLeft: "3%",
                          fontSize: "1rem",
                          backgroundColor: "#a4d4cc",
                          color: "black",
                        }}
                        onClick={handleMakeAReservation}
                      >
                        Reserve
                      </Button>
                    </div>
                  </div>
                </DialogContent>
              </Dialog> */}

              <Dialog
                open={openForm}
                // dividers={true}
                onClose={handleCloseForm}
                aria-labelledby="form-dialog-title"
                className="custom-dialog"
                maxWidth="sm"
                fullWidth
              >
                <DialogContent>
                  <div>
                    <h1 style={{ marginBottom: "3%", fontSize: "1.6rem" }}>
                      Password Recovery
                    </h1>
                    <hr
                      style={{
                        borderTop: "1px solid grey",
                        marginBottom: "3%",
                        width: "100%",
                      }}
                    />
                    {isLoadingMail ? (
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "center",
                          alignItems: "center",
                          marginTop: "10%",
                        }}
                      >
                        <CircularProgress style={{ color: "#a4d4cc" }} />
                      </Box>
                    ) : (
                      <>
                        <p>
                          Please enter your email to receive a link to reset
                          your password
                        </p>
                        <TextField
                          required
                          id="name"
                          value={email}
                          onChange={handleInputChange}
                          variant="outlined"
                          error={errors.email ? true : false}
                          helperText={errors.email || ""}
                          style={{
                            width: "80%",
                            marginTop: "3%",
                            marginBottom: "3%",
                            fontSize: "1.1rem",
                          }}
                          InputProps={{
                            style: {
                              fontSize: "1rem",
                            },
                          }}
                          FormHelperTextProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                        />

                        <div
                          className="buttons"
                          style={{
                            flex: 1,
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "flex-end",
                          }}
                        >
                          <Button
                            variant="outlined"
                            style={{
                              color: "grey",
                              borderColor: "grey",
                              fontSize: "1rem",
                            }}
                            onClick={handleCloseForm}
                          >
                            Cancel
                          </Button>
                          <Button
                            variant="contained"
                            style={{
                              marginLeft: "3%",
                              fontSize: "1rem",
                              backgroundColor: "#a4d4cc",
                              color: "black",
                            }}
                            onClick={handleSendEmail}
                          >
                            Send
                          </Button>
                        </div>
                      </>
                    )}
                  </div>
                </DialogContent>
              </Dialog>
              <Dialog
                open={openChangePasswordForm}
                // dividers={true}
                onClose={handleCloseChangePasswordForm}
                aria-labelledby="form-dialog-title"
                className="custom-dialog"
                maxWidth="sm"
                fullWidth
              >
                <DialogContent>
                  <div>
                    <h1 style={{ marginBottom: "3%", fontSize: "1.6rem" }}>
                      Change Password
                    </h1>
                    <hr
                      style={{
                        borderTop: "1px solid lightgrey",
                        marginBottom: "3%",
                        width: "100%",
                      }}
                    />
                    {isLoadingPin ? (
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "center",
                          alignItems: "center",
                          marginTop: "10%",
                        }}
                      >
                        <CircularProgress style={{ color: "#a4d4cc" }} />
                      </Box>
                    ) : (
                      <>
                        <p style={{ color: errors.email ? "red" : "black" }}>
                          Email *
                        </p>
                        <TextField
                          required
                          id="name"
                          value={email}
                          onChange={handleInputChange}
                          variant="outlined"
                          error={errors.email ? true : false}
                          helperText={errors.email || ""}
                          style={{
                            width: "80%",
                            marginTop: "3%",
                            marginBottom: "3%",
                            fontSize: "1.1rem",
                          }}
                          InputProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                          FormHelperTextProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                        />
                        <p style={{ color: errors.pin ? "red" : "black" }}>
                          Pin *
                        </p>
                        <TextField
                          required
                          id="name"
                          value={pin}
                          onChange={handlePinChange}
                          variant="outlined"
                          error={errors.pin ? true : false}
                          helperText={errors.pin || ""}
                          style={{
                            width: "80%",
                            marginTop: "3%",
                            marginBottom: "3%",
                            fontSize: "1.1rem",
                          }}
                          InputProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                          FormHelperTextProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                        />
                        <p style={{ color: errors.password ? "red" : "black" }}>
                          New Password *
                        </p>
                        <TextField
                          required
                          id="password"
                          type="password"
                          value={password}
                          onChange={handlePasswordChange}
                          variant="outlined"
                          error={errors.password ? true : false}
                          helperText={errors.password || ""}
                          style={{
                            width: "80%",
                            marginTop: "3%",
                            marginBottom: "3%",
                            fontSize: "1.1rem",
                          }}
                          InputProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                          FormHelperTextProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                        />
                        <p style={{ color: errors.password ? "red" : "black" }}>
                          Repeat Password *
                        </p>
                        <TextField
                          required
                          id="password"
                          type="password"
                          value={repeatPassword}
                          onChange={handleRepeatPasswordChange}
                          onCut={handleChange}
                          onCopy={handleChange}
                          onPaste={handleChange}
                          variant="outlined"
                          error={errors.repeatPassword ? true : false}
                          helperText={errors.repeatPassword || ""}
                          style={{
                            width: "80%",
                            marginTop: "3%",
                            marginBottom: "3%",
                            fontSize: "1.1rem",
                          }}
                          InputProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                          FormHelperTextProps={{
                            style: {
                              fontSize: "1.1rem",
                            },
                          }}
                        />
                        <div
                          className="buttons"
                          style={{
                            flex: 1,
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "flex-end",
                          }}
                        >
                          <Button
                            variant="outlined"
                            style={{
                              color: "grey",
                              borderColor: "grey",
                              fontSize: "1rem",
                            }}
                            onClick={handleCloseChangePasswordForm}
                          >
                            Cancel
                          </Button>
                          <Button
                            variant="contained"
                            style={{
                              marginLeft: "3%",
                              fontSize: "1rem",
                              backgroundColor: "#a4d4cc",
                              color: "black",
                            }}
                            onClick={handleChangePassword}
                          >
                            Change Password
                          </Button>
                        </div>
                      </>
                    )}
                  </div>
                </DialogContent>
              </Dialog>
            </form>
          </div>

          <div className="imageDiv">
            <img src={image} className="imag" alt="logo"></img>

            <div className="textDiv">
              <h2 className="title">Welcome Back</h2>
              <p className="subtitle">
                Please log in to your account to get started
              </p>
            </div>

            <div className="footerDiv flx">
              <span className="text">Don't have an account?</span>
              <Link to={"/register"}>
                <button className="btn">Sign Up</button>
              </Link>
            </div>
          </div>
        </div>

        {SignInPopUp && (
          <SInPopUp
            setSignInPopUp={setSignInPopUp}
            isRegistered={isRegistered}
          />
        )}
      </div>

      <Stack spacing={2} sx={{ width: "100%" }}>
        <Snackbar
          open={open}
          autoHideDuration={2500}
          onClose={handleSnackClose}
          anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
        >
          <Alert2
            onClose={handleSnackClose}
            severity="error"
            sx={{ width: "100%" }}
          >
            <h2 style={{ fontFamily: "Roboto", fontSize: "1rem" }}>
              One or more fields are empty/incorrect
            </h2>
          </Alert2>
        </Snackbar>
      </Stack>
      <Snackbar
        open={openSnackbar}
        autoHideDuration={10000}
        onClose={() => setOpenSnackbar(false)}
        anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
      >
        <Alert
          onClose={() => setOpenSnackbar(false)}
          variant="filled"
          severity={isOperationSuccessful ? "success" : "error"}
          sx={{ fontSize: "100%" }}
        >
          {alertText}
        </Alert>
      </Snackbar>
    </div>
  );
}

export default Login;
