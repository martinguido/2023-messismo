import React, { useEffect } from "react";
import "./Products.css";
import { useState } from "react";
import { IconButton } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import shiftService from "../services/shift.service";
import barService from "../services/bar.service";
import { useSelector } from "react-redux";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import Tooltip from "@mui/material/Tooltip";
import CircularProgress from "@mui/material/CircularProgress";
import Box from "@mui/material/Box";
import CapacityValidation from "../CapacityValidation";
import AddAShiftValidation from "../AddAShiftValidation";
import "./Products.css";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { TimeField } from "@mui/x-date-pickers/TimeField";

const BarConfigurationLists = () => {
  const [shifts, setShifts] = useState([]);
  const { user: currentUser } = useSelector((state) => state.auth);
  const role = currentUser.role;
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [alertText, setAlertText] = useState("");
  const [isOperationSuccessful, setIsOperationSuccessful] = useState(false);
  const [newCapacity, setNewCapacity] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [barCapacity, setBarCapacity] = useState("");
  const [openBarCapacityForm, setOpenBarCapacityForm] = useState(false);
  const [errors, setErrors] = useState({});
  const [modified, setModified] = useState(0);
  const [selectedShift, setSelectedShift] = useState("");
  const [openDeleteForm, setOpenDeleteForm] = useState(false);
  const [openCreateShiftModal, setOpenCreateShiftModal] = useState(false);
  const [startingTime, setStartingTime] = useState("");
  const [finishingTime, setFinishingTime] = useState("");

  useEffect(() => {
    shiftService
      .getAllShifts()
      .then((response) => {
        setShifts(response.data);
      })
      .catch((error) => {
        console.error("Error al mostrar los turnos", error);
      });
    barService
      .getBarConfiguration()
      .then((response) => {
        setBarCapacity(response.data);
        setIsLoading(false);
      })
      .catch((error) => {
        console.error("Error al mostrar la capacidad", error);
        setIsLoading(false);
      });
  }, [modified]);

  const deleteShift = async () => {
    if (selectedShift) {
      try {
        const response = await shiftService.deleteShift(selectedShift.shiftId);
        setSelectedShift(null);
        setIsOperationSuccessful(true);
        setAlertText("Shift deleted successfully");
        setModified(modified + 1);
        setOpenSnackbar(true);
      } catch (error) {
        setIsOperationSuccessful(false);
        setAlertText(error.response.data);
        setOpenSnackbar(true);
      }
      setOpenDeleteForm(false);
    }
  };
  const addShift = async () => {
    const validationErrors = AddAShiftValidation({
      startingTime: startingTime,
      finishingTime: finishingTime,
    });

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
    } else {
      try {
        const newStartingHour =
          startingTime.$H.toString().length === 1
            ? "0" + startingTime.$H.toString()
            : startingTime.$H.toString();
        const newFinishingHour =
          finishingTime.$H.toString().length === 1
            ? "0" + finishingTime.$H.toString()
            : finishingTime.$H.toString();
        const newStartingMinute =
          startingTime.$m.toString().length === 1
            ? "0" + startingTime.$m.toString()
            : startingTime.$m.toString();
        const newFinishingMinute =
          finishingTime.$m.toString().length === 1
            ? "0" + finishingTime.$m.toString()
            : finishingTime.$m.toString();

        const newShift = {
          startingHour: newStartingHour + ":" + newStartingMinute,
          finishingHour: newFinishingHour + ":" + newFinishingMinute,
        };
        const response = await shiftService.addShift(newShift);
        setIsOperationSuccessful(true);
        setAlertText("Shift added successfully");
        setModified(modified + 1);
        setOpenSnackbar(true);
        setFinishingTime("");
        setStartingTime("");
      } catch (error) {
        setIsOperationSuccessful(false);
        if (error.response.data === "") {
          setAlertText("Error creating this shift");
        } else {
          setAlertText(error.response.data);
        }
        setFinishingTime("");
        setStartingTime("");
        setOpenSnackbar(true);
      }
      handleCloseCreateShiftModal();
    }
  };
  const handleCloseDeleteForm = () => {
    setOpenDeleteForm(false);
    setIsLoading(true);
  };

  const handleInputCapacityChange = (e) => {
    setNewCapacity(e.target.value);
  };

  const handleDeleteClick = (shift) => {
    setSelectedShift(shift);
    setOpenDeleteForm(true);
  };
  const handleNewCapacity = () => {
    const validationErrors = CapacityValidation({
      capacity: newCapacity,
    });

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      console.log(validationErrors);
    } else {
      const modifyBarCapacityDTO = {
        barId: barCapacity.barId,
        newCapacity: newCapacity,
      };
      barService
        .modifyBarCapacity(modifyBarCapacityDTO)
        .then((response) => {
          setAlertText("Bar capacity updated successfully");
          setIsOperationSuccessful(true);
          setOpenSnackbar(true);
          handleCloseBarCapacityModal();
          setModified(modified + 1);
        })
        .catch((error) => {
          setAlertText(error.response.data);
          setIsOperationSuccessful(false);
          setOpenSnackbar(true);
        });
      setNewCapacity("");
    }
  };

  const handleOpenBarCapacityModal = () => {
    setOpenBarCapacityForm(true);
  };

  const handleCloseBarCapacityModal = () => {
    setOpenBarCapacityForm(false);
  };

  const handleOpenCreateShiftModal = () => {
    setOpenCreateShiftModal(true);
  };

  const handleCloseCreateShiftModal = () => {
    setOpenCreateShiftModal(false);
  };

  return (
    <div className="container">
      <div className="firstRow">
        <div
          style={{
            marginTop: "3.5%",
            display: "flex",
            flexDirection: "row",
            justifyContent: "flex-start",
            alignItems: "center",
          }}
        >
          <h2 style={{ color: "white", fontSize: "1.3rem" }}>Bar capacity:</h2>
          <h2
            style={{
              color: "white",
              fontSize: "1.3rem",
              marginRight: "3%",
              marginLeft: "3%",
            }}
          >
            {barCapacity.capacity}
          </h2>
          {role === "ADMIN" ? (
            <Button
              variant="contained"
              endIcon={<EditIcon />}
              style={{
                backgroundColor: "#a4d4cc",
                color: "black",
                marginLeft: "3%",
                borderColor: "#007bff",
                fontSize: "1rem",
                height: "50px",
              }}
              onClick={handleOpenBarCapacityModal}
            >
              Edit capacity
            </Button>
          ) : (
            <></>
          )}
        </div>
      </div>
      <div
        className="firstRow"
        style={{
          marginTop: "3%",
          display: "flex",
          justifyContent: "flex-end",
        }}
      >
        {role === "ADMIN" ? (
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
            onClick={handleOpenCreateShiftModal}
          >
            Add Shift
          </Button>
        ) : (
          <></>
        )}
      </div>
      <div className="titles">
        <div className="title">
          <p style={{ color: "white", fontWeight: "bold" }}>Shift details</p>
        </div>
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
          {shifts.map((shift, index) => (
            <div className="entradas" key={index}>
              <div className="product">
                <div className="firstLine">
                  <div className="names">
                    <div className="name">
                      <p
                        className="text"
                        style={{ fontWeight: "bold", height: "40px" }}
                      >
                        Shift time:{" "}
                        {shift.startingHour + " - " + shift.finishingHour}
                      </p>
                    </div>
                  </div>
                  <div className="buttons-edit">
                    {role === "ADMIN" ? (
                      <Tooltip
                        title="Delete Shift"
                        arrow
                        style={{ fontSize: "2rem" }}
                      >
                        <IconButton
                          aria-label="delete"
                          size="large"
                          style={{ color: "red", fontSize: "1.5 rem" }}
                          onClick={() => handleDeleteClick(shift)}
                        >
                          <DeleteIcon style={{ fontSize: "1.5rem" }} />
                        </IconButton>
                      </Tooltip>
                    ) : (
                      <div></div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </>
      )}

      {openBarCapacityForm && (
        <Dialog
          open={openBarCapacityForm}
          onClose={handleCloseBarCapacityModal}
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
            <p style={{ fontSize: "1.3rem" }}>New bar capacity</p>
            <TextField
              required
              id="name"
              value={newCapacity}
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
          </DialogTitle>
          <DialogActions>
            <Button
              onClick={handleCloseBarCapacityModal}
              style={{ fontSize: "1.1rem" }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleNewCapacity}
              style={{
                backgroundColor: "#a4d4cc",
                color: "black",
                borderColor: "#007bff",
                marginTop: "4%",
                fontSize: "1.1rem",
                height: "50px",
              }}
              autoFocus
            >
              Update
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {openDeleteForm && (
        <Dialog
          open={openDeleteForm}
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
                Are you sure you want to delete the shift{" "}
                <strong>
                  {selectedShift.startingHour +
                    " - " +
                    selectedShift.finishingHour}
                </strong>
                ?
              </p>
            )}
          </DialogTitle>
          <DialogContent>
            <DialogContentText
              id="alert-dialog-description"
              style={{ fontSize: "1.2rem" }}
            >
              The shift will be permanently deleted
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
              onClick={deleteShift}
              style={{ color: "red", fontSize: "1.1rem" }}
              autoFocus
            >
              Delete
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {openCreateShiftModal && (
        <Dialog
          open={openCreateShiftModal}
          onClose={handleCloseCreateShiftModal}
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
          {/* <DialogTitle
            id="alert-dialog-title"
            style={{ fontSize: "1.5rem" }}
          ></DialogTitle> */}
          <DialogContent>
            <h1 style={{ marginBottom: "3%", fontSize: "1.6rem" }}>
              Add a shift
            </h1>
            <hr
              style={{
                borderTop: "1px solid grey",
                marginBottom: "3%",
                width: "100%",
              }}
            />
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <p style={{ marginTop: "7.5%" }}>
                Please enter a starting hour *
              </p>
              <TimeField
                label="HH:MM"
                variant="outlined"
                error={errors.startingTime ? true : false}
                helperText={errors.startingTime || ""}
                value={startingTime}
                onChange={(newValue) => setStartingTime(newValue)}
                format="HH:mm"
                style={{ marginTop: "7.5%" }}
              />
              <p style={{ marginTop: "7.5%" }}>
                Please enter a finishing hour *
              </p>
              <TimeField
                label="HH:MM"
                variant="outlined"
                error={errors.finishingTime ? true : false}
                helperText={errors.finishingTime || ""}
                value={finishingTime}
                onChange={(newValue) => setFinishingTime(newValue)}
                format="HH:mm"
                style={{ marginTop: "7.5%" }}
              />
            </LocalizationProvider>
          </DialogContent>
          <DialogActions>
            <Button
              onClick={handleCloseCreateShiftModal}
              style={{ fontSize: "1.1rem" }}
            >
              Cancel
            </Button>
            <Button
              onClick={addShift}
              style={{
                backgroundColor: "#a4d4cc",
                color: "black",
                borderColor: "#007bff",
                fontSize: "1.1rem",
              }}
              autoFocus
            >
              Add Shift
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

export default BarConfigurationLists;
