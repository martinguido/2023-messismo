import React, { useEffect, useState } from "react";
import { IconButton } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import TextField from "@mui/material/TextField";
import DialogTitle from "@mui/material/DialogTitle";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import reservationService from "../services/reservation.service";
import { useSelector } from "react-redux";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import Tooltip from "@mui/material/Tooltip";
import CircularProgress from "@mui/material/CircularProgress";
import Box from "@mui/material/Box";
import MakeAReservationValidation from "../MakeAReservationValidation";
import MenuItem from "@mui/material/MenuItem";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import shiftService from "../services/shift.service";
import Select from "@mui/material/Select";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";

const ProductsList = () => {
  const [reservations, setReservations] = useState([]);

  const { user: currentUser } = useSelector((state) => state.auth);
  const role = currentUser.role;
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertText, setAlertText] = useState("");
  const [isOperationSuccessful, setIsOperationSuccessful] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedShift, setSelectedShift] = useState("");
  const [shifts, setShifts] = useState([]);
  const [emailR, setEmailR] = useState("");
  const [phone, setPhone] = useState("");
  const [comment, setComment] = useState("");
  const [capacity, setCapacity] = useState("");
  const [openReservationForm, setOpenReservationForm] = useState(false);
  const [errors, setErrors] = useState({});
  const [modified, setModified] = useState(0);
  const [openDeleteReservationForm, setOpenDeleteReservationForm] =
    useState(false);

  useEffect(() => {
    setIsLoading(true);
    reservationService
      .getAllReservations()
      .then((response) => {
        setReservations(response.data);
        setIsLoading(false);
      })
      .catch((error) => {
        console.error("Error al mostrar las reservas", error);
        setIsLoading(false);
      });
  }, [openDeleteReservationForm, openReservationForm, modified]);
  useEffect(() => {
    setIsLoading(true);
    shiftService
      .getAllShifts()
      .then((response) => {
        setShifts(response.data);
        setIsLoading(false);
      })
      .catch((error) => {
        console.error("Error al obtener turnos:", error);
        setIsLoading(false);
      });
  }, [openReservationForm]);
  const handleDeleteClick = (aReservation) => {
    setSelectedReservation(aReservation);
    setOpenDeleteReservationForm(true);
  };

  const handleCloseDeleteForm = () => {
    setOpenDeleteReservationForm(false);
  };

  const deleteReservation = async () => {
    if (selectedReservation) {
      try {
        setIsLoading(true);
        const response = await reservationService.deleteReservation(
          selectedReservation.reservationId
        );
        setSelectedReservation(null);
        setIsOperationSuccessful(true);
        setAlertText("Reservation deleted successfully");
        setOpenDeleteReservationForm(false);
        setOpenSnackbar(true);
        setIsLoading(false);
        setModified(modified + 1);
      } catch (error) {
        setIsLoading(false);
        console.error("Error al eliminar la reserva", error);
        setIsOperationSuccessful(false);
        setOpenDeleteReservationForm(false);
        setAlertText("Failed to delete reservation");
        setOpenSnackbar(true);
        setModified(modified + 1);
      }
    }
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
      console.log(validationErrors);
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
          setModified(modified + 1);
          setErrors({});
        })
        .catch((error) => {
          setAlertText(error.response.data);
          setIsOperationSuccessful(false);
          setOpenSnackbar(true);
          setModified(modified + 1);
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
    <div className="container">
      <div
        className="firstRow"
        style={{
          marginTop: "3%",
          display: "flex",
          justifyContent: "flex-end",
        }}
      >
        <Button
          variant="contained"
          endIcon={<AddIcon />}
          style={{
            backgroundColor: "#a4d4cc",
            color: "black",
            borderColor: "#007bff",
            fontSize: "1rem",
            height: "50px",
          }}
          onClick={handleMakeAReservationOpenForm}
        >
          Make a Reservation
        </Button>
      </div>
      <div className="titles">
        <div className="title">
          <p style={{ color: "white", fontWeight: "bold" }}>Date</p>
        </div>
        <div className="title">
          <p style={{ color: "white", fontWeight: "bold" }}>Shift</p>
        </div>
        <div className="title">
          <p style={{ color: "white", fontWeight: "bold" }}>Client Email</p>
        </div>
        <div className="title">
          <p style={{ color: "white", fontWeight: "bold" }}>Client Phone</p>
        </div>
        <div className="title">
          <p style={{ color: "white", fontWeight: "bold" }}>Capacity</p>
        </div>
        <div className="title">
          <p style={{ color: "white", fontWeight: "bold" }}>Comment</p>
        </div>
        {role === "ADMIN" || role === "MANAGER" ? (
          <div className="title">
            <p style={{ color: "white", fontWeight: "bold" }}>Action</p>
          </div>
        ) : (
          <div></div>
        )}
      </div>
      {isLoading ? (
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
          {reservations.map((reservation, index) => (
            <div className="entradas" key={index}>
              <div className="product">
                <div className="firstLine">
                  <div className="names">
                    <div className="name">
                      <p
                        className="text reservet"
                        style={{ fontWeight: "bold" }}
                      >
                        {reservation.reservationId +
                          " : " +
                          reservation.startingDate.substring(0, 10)}
                      </p>
                    </div>
                    <div className="category">
                      <p className="text reservet">
                        {reservation.startingDate.substring(11, 16) +
                          " - " +
                          reservation.finishingDate.substring(11, 16)}
                      </p>
                    </div>
                    {reservation.clientEmail !== null ? (
                      <div className="category">
                        <p className="text reservet">
                          {reservation.clientEmail}
                        </p>
                      </div>
                    ) : (
                      <div className="category">
                        <p className="text reservet"> - </p>
                      </div>
                    )}

                    {reservation.clientPhone !== null ? (
                      <div className="category">
                        <p className="text reservet">
                          {reservation.clientPhone}
                        </p>
                      </div>
                    ) : (
                      <div className="category">
                        <p className="text reservet"> - </p>
                      </div>
                    )}
                    <div className="category">
                      <p className="text reservet">{reservation.capacity}</p>
                    </div>
                    <div className="category">
                      <p className="text reservet">{reservation.comment}</p>
                    </div>
                    {role === "ADMIN" || role === "MANAGER" ? (
                      <div className="category">
                        <Tooltip
                          title="Delete Reservation"
                          arrow
                          style={{ fontSize: "2rem" }}
                        >
                          <IconButton
                            aria-label="delete"
                            size="large"
                            style={{ color: "red", fontSize: "1.5 rem" }}
                            onClick={() => handleDeleteClick(reservation)}
                          >
                            <DeleteIcon style={{ fontSize: "1.5rem" }} />
                          </IconButton>
                        </Tooltip>
                      </div>
                    ) : (
                      <div></div>
                    )}
                  </div>
                </div>
                <div className="final-line"></div>
              </div>
            </div>
          ))}
        </>
      )}
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
      {openDeleteReservationForm && (
        <Dialog
          open={openDeleteReservationForm}
          onClose={handleCloseDeleteForm}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description"
          PaperProps={{
            style: {
              backgroundColor: "white",
              boxShadow: "none",
              zIndex: 1000,
              fontSize: "24px",
            },
          }}
        >
          <DialogTitle id="alert-dialog-title" style={{ fontSize: "1.5rem" }}>
            {selectedShift && (
              <p style={{ fontSize: "1.3rem" }}>
                Are you sure you want to delete the reservation
              </p>
            )}
          </DialogTitle>
          <DialogContent>
            <DialogContentText
              id="alert-dialog-description"
              style={{ fontSize: "1.2rem" }}
            >
              The reservation will be permanently deleted
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button
              onClick={handleCloseDeleteForm}
              style={{ fontSize: "1.1rem" }}
            >
              Cancel
            </Button>
            <Button
              onClick={deleteReservation}
              style={{ color: "red", fontSize: "1.1rem" }}
              autoFocus
            >
              Delete
            </Button>
          </DialogActions>
        </Dialog>
      )}
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
          sx={{ fontSize: "75%" }}
        >
          {alertText}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default ProductsList;
