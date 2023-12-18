import React, { useEffect, useState } from "react";
// import { IconButton } from "@mui/material";
// import DeleteIcon from "@mui/icons-material/Delete";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import { AiFillCheckCircle } from "react-icons/ai";
import { AiFillCloseCircle } from "react-icons/ai";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import TextField from "@mui/material/TextField";
import DialogTitle from "@mui/material/DialogTitle";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import reservationService from "../services/reservation.service";
// import { useSelector } from "react-redux";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
// import Tooltip from "@mui/material/Tooltip";
import moment from "moment";
// import { useTheme } from "@mui/material/styles";
import {
  Box,
  Typography,
  // gridClasses,
  // useMediaQuery,
} from "@mui/material";
import CircularProgress from "@mui/material/CircularProgress";
import MakeAReservationValidation from "../MakeAReservationValidation";
import MenuItem from "@mui/material/MenuItem";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import shiftService from "../services/shift.service";
import Select from "@mui/material/Select";
import { DataGrid } from "@mui/x-data-grid";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AiFillDelete } from "react-icons/ai";
// import { AiFillCheckCircle } from "react-icons/ai";
// import { AiFillCloseCircle } from "react-icons/ai";
import Fab from "@mui/material/Fab";
import dayjs from "dayjs";
// import EditIcon from "@mui/icons-material/Edit";

// const ALL_COLUMNS = {
//   id: true,
//   shift: true,
//   date: true,
//   email: true,
//   phone: true,
//   capacity: true,
//   comment: true,
//   state: true,
//   used: true,
// };
const tomorrow = dayjs().add(1, "day");
const ProductsList = () => {
  const [reservations, setReservations] = useState([]);

  // const { user: currentUser } = useSelector((state) => state.auth);
  // const role = currentUser.role;
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
  const [pageSize, setPageSize] = useState(5);
  const [modified, setModified] = useState(0);
  // eslint-disable-next-line
  const [initialRows, setInitialRows] = useState([]);
  // eslint-disable-next-line
  const [selectedRow, setSelectedRow] = useState(initialRows);
  const [isLoadingReservation, setIsLoadingReservation] = useState(false);
  const [isLoadingShift, setIsLoadingShift] = useState(false);
  const [openDeleteReservationForm, setOpenDeleteReservationForm] =
    useState(false);
  const [openMarkAsUsedReservationForm, setOpenMarkAsUsedReservationForm] =
    useState(false);
  // const theme = useTheme();
  // const matches = useMediaQuery(theme.breakpoints.up("sm"));

  // const [columnVisible, setColumnVisible] = useState(ALL_COLUMNS);
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
  }, [modified]);

  // useEffect(() => {
  //   setIsLoading(true);
  //   shiftService
  //     .getAllShifts()
  //     .then((response) => {
  //       setShifts(response.data);
  //       setIsLoading(false);
  //     })
  //     .catch((error) => {
  //       console.error("Error al obtener turnos:", error);
  //       setIsLoading(false);
  //     });
  // }, [openReservationForm]);

  const handleDeleteClick = (aReservation) => {
    setSelectedReservation(aReservation);
    setOpenDeleteReservationForm(true);
  };

  const handleMarkAsUsedClick = (aReservation) => {
    setSelectedReservation(aReservation);
    setOpenMarkAsUsedReservationForm(true);
  };
  const handleCloseUsedForm = () => {
    setOpenMarkAsUsedReservationForm(false);
  };

  const handleCloseDeleteForm = () => {
    setOpenDeleteReservationForm(false);
  };
  const handleRowClick = (params) => {
    setSelectedRow(params.row);
  };

  const deleteReservation = async () => {
    if (selectedReservation) {
      try {
        setIsLoading(true);
        await reservationService.deleteReservation(selectedReservation.id);
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

  const usedReservation = async () => {
    if (selectedReservation) {
      try {
        setIsLoading(true);
        await reservationService.markAsUsedReservaiton(selectedReservation.id);
        setSelectedReservation(null);
        setIsOperationSuccessful(true);
        setAlertText("Reservation use successfully!");
        setOpenMarkAsUsedReservationForm(false);
        setOpenSnackbar(true);
        setIsLoading(false);
        setModified(modified + 1);
      } catch (error) {
        setIsLoading(false);
        console.error("Error al eliminar la reserva", error);
        setIsOperationSuccessful(false);
        setOpenMarkAsUsedReservationForm(false);
        setAlertText("Failed to mark as used reservation");
        setOpenSnackbar(true);
        setModified(modified + 1);
      }
    }
  };

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
          setModified(modified + 1);
          setErrors({});
        })
        .catch((error) => {
          setAlertText(error.response.data);
          setIsOperationSuccessful(false);
          setOpenSnackbar(true);
          setModified(modified + 1);
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

  const rows = reservations.map((reservation) => ({
    id: reservation.reservationId,
    shift:
      reservation.shift.startingHour + " - " + reservation.shift.finishingHour,
    date: reservation.reservationDate,
    email: reservation.clientEmail === null ? "-" : reservation.clientEmail,
    phone: reservation.clientPhone === null ? "-" : reservation.clientPhone,
    capacity: reservation.capacity,
    comment: reservation.comment,
    state: reservation.state,
    used: reservation.used,
  }));
  const columns = [
    {
      field: "id",
      headerName: "Id",
      // flex: 0.3,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 30,
    },
    {
      field: "shift",
      headerName: "Shift",
      // flex: 0.8,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 200,
    },
    {
      field: "date",
      headerName: "Date",
      // flex: 0.8,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 70,
      renderCell: (params) => moment(params.row.date).format("YYYY-MM-DD"),
    },
    {
      field: "email",
      headerName: "Email",
      // flex: 0.8,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 300,
    },
    {
      field: "phone",
      headerName: "Phone",
      // flex: 0.8,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 150,
    },
    {
      field: "capacity",
      headerName: "Capacity",
      // flex: 1,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 150,
    },
    {
      field: "comment",
      headerName: "Comment",
      flex: 2,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 300,
    },

    {
      field: "state",
      headerName: "State",
      flex: 1,
      align: "center",
      headerAlign: "center",
      sortable: true,
      minWidth: 250,
      renderCell: (params) => {
        const status = params.row.state;
        const statusColors = {
          Upcoming: "#FDFEBC",
          Expired: "#FEBCBC",
          InProcess: "#4C5C68",
        };

        const fontColors = {
          Upcoming: "black",
          Expired: "black",
          InProcess: "white",
        };

        const backgroundColor = statusColors[status] || "white";
        const fontColor = fontColors[status] || "black";

        const statusStyle = {
          backgroundColor,
          color: fontColor,
          textAlign: "center",
          padding: "8px",
          fontSize: "0.9rem",
          textTransform: "none",
          width: "50%",
          height: "50%",
        };

        return (
          <Fab
            variant="extended"
            size="small"
            color="primary"
            style={statusStyle}
            disabled={true}
          >
            <p>{status}</p>
          </Fab>
        );
      },
    },
    // {
    //   field: "used",
    //   headerName: "Used",
    //   flex: 1,
    //   align: "center",
    //   headerAlign: "center",
    //   sortable: true,
    //   minWidth: 250,
    //   renderCell: (params) => {
    //     const status = params.row.used.toString();
    //     let state = "";
    //     if (status === "false") {
    //       state = "Not yet";
    //     }
    //     if (status === "true") {
    //       state = "Yes";
    //     }
    //     const statusColors = {
    //       true: "#FDFEBC",
    //       false: "#FEBCBC",
    //     };

    //     const backgroundColor = statusColors[status] || "green";
    //     const fontColor = "black";

    //     const statusStyle = {
    //       backgroundColor,
    //       color: fontColor,
    //       textAlign: "center",
    //       padding: "8px",
    //       fontSize: "0.9rem",
    //       textTransform: "none",
    //       width: "50%",
    //       height: "50%",
    //     };

    //     return (
    //       <Fab
    //         variant="extended"
    //         size="small"
    //         color="primary"
    //         style={statusStyle}
    //         disabled={true}
    //       >
    //         <p>{state}</p>
    //       </Fab>
    //     );
    //   },
    // },
    {
      field: "used",
      headerName: "Used",
      flex: 1,
      align: "center",
      headerAlign: "center",
      sortable: true,
      maxWidth: 100,
      renderCell: (params) => {
        const status = params.row.used;
        if (status) {
          return (
            <div style={{ textAlign: "center" }}>
              <AiFillCheckCircle
                style={{ color: "rgba(159,193,108)", fontSize: "2rem" }}
              />
            </div>
          );
        } else if (!status) {
          return (
            <div style={{ textAlign: "center" }}>
              <AiFillCloseCircle
                style={{ color: "rgba(212,150,187)", fontSize: "2rem" }}
              />
            </div>
          );
        }

        return null;
      },
    },
    {
      field: "markAsUsed",
      headerName: "",
      flex: 1,
      align: "center",
      headerAlign: "center",
      sortable: false,
      maxWidth: 80,
      renderCell: (params) => {
        const status = params.row.used;
        const isInProcess = status === true;

        return (
          <button
            onClick={() => handleMarkAsUsedClick(params.row)}
            disabled={isInProcess}
          >
            <EditIcon style={{ fontSize: "2rem" }} />
          </button>
        );
      },
    },
    {
      field: "remove",
      headerName: "",
      flex: 1,
      align: "center",
      headerAlign: "center",
      sortable: false,
      maxWidth: 80,
      renderCell: (params) => {
        return (
          <button onClick={() => handleDeleteClick(params.row)}>
            <AiFillDelete style={{ fontSize: "2rem" }} />
          </button>
        );
      },
    },
  ];

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
      <div
        style={{
          width: "93%",
          marginLeft: "3%",
          marginRight: "3%",
        }}
      >
        <div>
          <Typography
            variant="h3"
            component="h3"
            sx={{
              textAlign: "center",
              mt: 3,
              mb: 3,
              color: "white",
            }}
          >
            Reservations
          </Typography>
          <DataGrid
            initialState={{
              pagination: { paginationModel: { pageSize: 5 } },
              sorting: {
                sortModel: [{ field: "date", sort: "desc" }],
              },
            }}
            autoHeight={true}
            columns={columns}
            // columnVisibilityModel={columnVisible}
            rows={rows}
            onRowClick={handleRowClick}
            pageSizeOptions={[5, 10, 25]}
            pagination
            pageSize={pageSize}
            onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
            getRowSpacing={(params) => ({
              top: params.isFirstVisible ? 0 : 5,
              bottom: params.isLastVisible ? 0 : 5,
            })}
            sx={{
              fontSize: "1rem",
              border: 2,
              borderColor: "#a4d4cc",
              "& .MuiButtonBase-root": {
                color: "white",
              },
              "& .MuiDataGrid-cell:hover": {
                color: "#a4d4cc",
              },
              ".MuiDataGrid-columnSeparator": {
                display: "none",
              },
              color: "white",
              fontFamily: "Roboto",
              ".MuiTablePagination-displayedRows": {
                color: "white",
                fontSize: "1.2rem",
              },
              ".MuiTablePagination-selectLabel": {
                color: "white",
                fontSize: "1.2rem",
              },
              "& .MuiSelect-select.MuiSelect-select": {
                color: "white",
                fontSize: "1.2rem",
                marginTop: "0.7rem",
              },
              ".MuiDataGrid-sortIcon": {
                opacity: "inherit !important",
                color: "white",
              },
              "& .MuiDataGrid-cell:focus": {
                outline: "none",
              },
              "@media (max-width: 1000px)": {
                fontSize: "1rem",
              },
              "@media (max-width: 760px)": {
                fontSize: "1rem",
              },
              "@media (max-width: 600px)": {
                fontSize: "1rem",
              },
              "@media (max-width: 535px)": {
                fontSize: "1.2rem",
              },
              "@media (max-width: 435px)": {
                fontSize: "1rem",
              },
              "@media (max-width: 335px)": {
                fontSize: "0.8rem",
              },
            }}
          />
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
          {reservations.length === 0 ? (
            <div>
              <h3 style={{ marginTop: "15%", color: "white" }}>
                No data to display
              </h3>
            </div>
          ) : (
            <div></div>
          )}
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
                <p style={{ marginTop: "3.5%" }}>Please enter a shift *</p>
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

            <p style={{ marginBottom: "3.5%" }}>Please enter a comment *</p>
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

      {openMarkAsUsedReservationForm && (
        <Dialog
          fullWidth={true}
          maxWidth="sm"
          open={openMarkAsUsedReservationForm}
          onClose={handleCloseUsedForm}
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
              <p style={{ fontSize: "1.3rem" }}>Mark reservation as used</p>
            )}
          </DialogTitle>
          <DialogContent>
            <DialogContentText
              id="alert-dialog-description"
              style={{ fontSize: "1.2rem" }}
            >
              The client has arrived?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button
              onClick={handleCloseUsedForm}
              style={{ fontSize: "1.1rem" }}
            >
              Cancel
            </Button>
            <Button
              onClick={usedReservation}
              style={{ color: "green", fontSize: "1.1rem" }}
              autoFocus
            >
              Yes
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
