import React, { useState, useEffect, forwardRef } from "react";
import "./Login.css";
import "../App.css";
import image from "../images/signup2.png";
import logo from "../images/logo.png";
import {
  Link,
  Navigate,
  // useNavigate
} from "react-router-dom";
import { FaUserShield } from "react-icons/fa";
import { BsFillShieldLockFill } from "react-icons/bs";
import { MdEmail } from "react-icons/md";
import styled from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import SUpPopUp from "../components/SignUpPopUp";
import signupvalidation from "../SignUpValidation";
import { register } from "../redux/auth";
import { clearMessage } from "../redux/message";
import Stack from "@mui/material/Stack";
import Snackbar from "@mui/material/Snackbar";
import MuiAlert from "@mui/material/Alert";
import MakeAReservationValidation from "../MakeAReservationValidation";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import TextField from "@mui/material/TextField";
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

const ErrorMessage = styled.h4`
  color: red;
  font-family: "Roboto";
  width: 240px;
  word-wrap: break-word;
`;

const Alert = forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const Register = () => {
  const [open, setOpen] = useState(false);

  const [isRegistered, setIsRegistered] = useState(false);

  const [isSignUpValid, setIsSignUpValid] = useState(false);
  const [signupvalues, setSignUpValues] = useState({
    username: "",
    email: "",
    password: "",
  });
  const [signuperrors, setSignUpErrors] = useState({});
  const [SignUpPopUp, setSignUpPopUp] = useState(false);
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedShift, setSelectedShift] = useState("");
  const [shifts, setShifts] = useState([]);
  const [emailR, setEmailR] = useState("");
  const [phone, setPhone] = useState("");
  const [comment, setComment] = useState("");
  const [capacity, setCapacity] = useState("");
  const [openReservationForm, setOpenReservationForm] = useState(false);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertText, setAlertText] = useState("");
  const [isOperationSuccessful, setIsOperationSuccessful] = useState(false);
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const { isLoggedIn } = useSelector((state) => state.auth);
  //   const { message } = useSelector((state) => state.message);
  const { user: currentUser } = useSelector((state) => state.auth);

  // let navigate = useNavigate();
  const dispatch = useDispatch();
  useEffect(() => {
    shiftService
      .getAllShifts()
      .then((response) => {
        setShifts(response.data);
      })
      .catch((error) => {
        console.error("Error al obtener turnos:", error);
      });
  }, [openReservationForm]);
  useEffect(() => {
    const validationErrors = signupvalidation(signupvalues);
    setSignUpErrors(validationErrors);
    setIsSignUpValid(Object.keys(validationErrors).length === 0);
  }, [signupvalues]);

  useEffect(() => {
    dispatch(clearMessage());
  }, [dispatch]);

  if (
    isLoggedIn &&
    (currentUser.role === "ADMIN" ||
      currentUser.role === "MANAGER" ||
      currentUser.role === "VALIDATEDEMPLOYEE")
  ) {
    return <Navigate to="/homepage" />;
  }

  const handleRegister = (userData) => {
    setIsLoading(true);
    const username = userData.username;
    const email = userData.email;
    const password = userData.password;

    dispatch(register({ username, email, password }))
      .unwrap()
      .then(() => {
        setIsRegistered(false);
        setSignUpPopUp(true);
        setSignUpValues({
          username: "",
          email: "",
          password: "",
        });
      })
      .catch(() => {
        setIsRegistered(true);
        setSignUpPopUp(true);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleSignUpInput = (e) => {
    setSignUpValues({ ...signupvalues, [e.target.name]: e.target.value });
  };

  const handleSignUpErrors = (e) => {
    setSignUpErrors((prevErrors) => ({
      ...prevErrors,
      [e.target.name]: "",
    }));
  };

  function handleSignUpValidation() {
    const validationErrors = signupvalidation(signupvalues);
    setSignUpErrors(validationErrors);
    if (Object.keys(validationErrors).length === 0) {
      setIsSignUpValid(true);
    } else {
      setIsSignUpValid(false);
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

  const handleDateChange = (date) => {
    setSelectedDate(date);
  };
  const handleMakeAReservationOpenForm = () => {
    setOpenReservationForm(true);
  };

  const handleMakeAReservationCloseForm = () => {
    setOpenReservationForm(false);
  };
  const handleInputEmailRChange = (e) => {
    setEmailR(e.target.value);
  };
  const handleInputPhoneChange = (e) => {
    setPhone(e.target.value);
  };
  const handleInputCapacityChange = (e) => {
    setCapacity(e.target.value);
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
    } else {
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
        startingDate: fechaSinHora,
        finishingDate: fechaSinHora,
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
        })
        .catch((error) => {
          setAlertText(error.response.data);
          setIsOperationSuccessful(false);
          setOpenSnackbar(true);
        });
      setCapacity("");
      setSelectedShift("");
      setSelectedDate("");
      setComment("");
      setEmailR("");
      setPhone("");
    }
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
                <label htmlFor="username" className="labl">
                  Username
                </label>
                <div className="inpt flx">
                  <FaUserShield className="icn" />
                  <input
                    type="text"
                    name="username"
                    id="usernameId"
                    placeholder="Enter your username"
                    className="inpt"
                    onChange={handleSignUpInput}
                    onBlur={handleSignUpErrors}
                  />
                </div>
                {signuperrors.username && (
                  <ErrorMessage>{signuperrors.username}</ErrorMessage>
                )}
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
                    id="emailSuId"
                    placeholder="Enter your email"
                    className="inpt"
                    onChange={handleSignUpInput}
                    onBlur={handleSignUpErrors}
                  />
                </div>
                {signuperrors.email && (
                  <ErrorMessage>{signuperrors.email}</ErrorMessage>
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
                    id="passwordSuId"
                    placeholder="Enter your password"
                    className="inpt"
                    onChange={handleSignUpInput}
                    onBlur={handleSignUpErrors}
                  />
                </div>
                {signuperrors.password && (
                  <ErrorMessage>{signuperrors.password}</ErrorMessage>
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
                type="submit"
                className="btn flx"
                onClick={() => {
                  handleSignUpValidation();
                  if (isSignUpValid) {
                    const userData = {
                      username: signupvalues.username,
                      email: signupvalues.email,
                      password: signupvalues.password,
                    };
                    handleRegister(userData);
                  }
                }}
                disabled={
                  Object.keys(signuperrors).length > 0 || !isSignUpValid
                }
              >
                <span>Sign Up</span>
              </Link>
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
                    <p>Please enter a comment *</p>
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
            </form>
          </div>
          <div className="imageDiv">
            <img src={image} className="imag" alt="logo"></img>

            <div className="textDiv">
              <h2 className="title">Hi There</h2>
              <p className="subtitle">
                Please note that admin verification is required to activate your
                account
              </p>
            </div>

            <div className="footerDiv flx">
              <span className="text">Already have an account?</span>
              <Link to={"/login"}>
                <button className="btn">Sign In</button>
              </Link>
            </div>
          </div>
        </div>

        {SignUpPopUp && (
          <SUpPopUp
            setSignUpPopUp={setSignUpPopUp}
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
          <Alert
            onClose={handleSnackClose}
            severity="error"
            sx={{ width: "100%" }}
          >
            <h2 style={{ fontFamily: "Roboto", fontSize: "1rem" }}>
              One or more fields are empty/incorrect
            </h2>
          </Alert>
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
};

export default Register;
