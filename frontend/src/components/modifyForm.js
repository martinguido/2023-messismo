import React, {
  useState,
  // useEffect
} from "react";
// import { useForm, Controller } from "react-hook-form";
import styled from "styled-components";
// import { GrAddCircle } from "react-icons/gr";
// import { RiDeleteBinLine } from "react-icons/ri";
// import productsService from "../services/products.service";
import ordersService from "../services/orders.service";
// import { useSelector } from "react-redux";
// import { propsToClassKey } from "@mui/styles";
import EditOrderForm from "./EditOrderForm";

const Form = styled.form`
  padding: 2rem;
  background-color: rgb(164, 212, 204, 0.6);

  .fail {
    color: red;
  }

  .form-totalprice {
    margin-top: 1.5rem;
    text-align: center;
    span {
      font-size: 24px;
    }
  }

  small {
    font-size: 10px;

    @media (max-width: 550px) {
      font-size: 9px;
    }

    @media (max-width: 450px) {
      font-size: 8px;
    }

    @media (max-width: 350px) {
      font-size: 7px;
    }
  }

  @media (max-width: 250px) {
    min-width: 250px;
  }
`;

// const ProductContainer = styled.div`
//   display: flex;
//   flex-direction: row;
//   justify-content: space-between;
//   margin-bottom: 2rem;

//   .form-product {
//     margin-right: 1rem;
//     width: 70%;
//   }

//   .form-amount {
//     margin-right: 1rem;
//     width: 15%;
//     text-align: center;
//   }

//   .form-price {
//     width: 15%;
//     overflow: hidden;
//     text-align: center;
//     margin-top: 3.5rem;

//     span {
//       font-size: 14px;

//       @media (max-width: 550px) {
//         font-size: 12px;
//       }

//       @media (max-width: 450px) {
//         font-size: 10px;
//       }

//       @media (max-width: 350px) {
//         font-size: 8px;
//       }
//     }
//   }
// `;

// const Label = styled.label`
//   display: inline-block;
//   margin-bottom: 7px;
//   font-size: 1.3rem;
//   text-transform: uppercase;
//   color: black;
// `;

// const Select = styled.select`
//   border: 1px solid rgb(164, 212, 204, 0.7);
//   background-color: transparent;
//   display: block;
//   font-family: inherit;
//   font-size: 16px;
//   padding: 1rem;
//   width: 100%;

//   &:focus {
//     outline: none;
//     border-color: #a4d4cc;
//   }

//   @media (max-width: 350px) {
//     font-size: 12px;
//   }
// `;

// const Input = styled.input`
//   border: 1px solid rgb(164, 212, 204, 0.7);
//   background-color: transparent;
//   display: block;
//   font-family: inherit;
//   font-size: 16px;
//   padding: 1.1rem;
//   width: 100%;
//   text-align: center;

//   &:focus {
//     outline: none;
//     border-color: #a4d4cc;
//   }

//   @media (max-width: 350px) {
//     font-size: 12px;
//   }
// `;

// const AddIcon = styled(GrAddCircle)`
//   cursor: pointer;
//   font-size: 20px;
//   width: 100%;
// `;

// const RemoveIcon = styled(RiDeleteBinLine)`
//     color: red;
//     font-size: 20px;
// `;

const Button = styled.button`
  display: block;
  width: 100%;
  font-size: 1.2rem;
  border-radius: 3px;
  padding: 1rem 3.5rem;
  margin-top: 2rem;
  border: 1px solid black;
  background-color: #a4d4cc;
  color: black;
  text-transform: uppercase;
  cursor: pointer;
  letter-spacing: 1px;
  box-shadow: 0 3px #999;
  font-family: "Roboto", serif;
  text-align: center;

  &:hover {
    background-color: #a7d0cd;
  }
  &:active {
    background-color: #a4d4cc;
    box-shadow: 0 3px #666;
    transform: translateY(4px);
  }
  &:focus {
    outline: none;
  }
`;

const Buttons = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .placeorder {
    margin-right: 1rem;
  }

  @media (max-width: 477px) {
    width: 100%;
    flex-direction: column;
    text-align: center;
  }
`;

const Details = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: 9999;
  padding: 1rem;
`;

const DetailsContent = styled.div`
  padding: 2rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  max-height: 60vh;
  overflow-y: auto;
  width: 50% !important;
  margin: auto;
  background-color: black;
  strong {
    color: white;
    font-family: "Roboto";
    font-size: 1.5rem;
  }
  strong2 {
    color: white;
    font-family: "Roboto";
    font-size: 1.7rem;
    margin-top: 1rem;
    align-self: center;
    margin-bottom: 1rem;
  }

  @media (max-width: 1500px) {
    width: 50% !important;
  }
  @media (max-width: 1000px) {
    width: 100% !important;
  }
  @media (max-width: 800px) {
    width: 100% !important;
  }
`;

const DetailsButton = styled.button`
  display: block;
  width: 50%;
  font-size: 1.2rem;
  border-radius: 3px;
  padding: 1rem 2rem;
  border: 1px solid black;
  background-color: #a4d4cc;
  color: black;
  text-transform: uppercase;
  cursor: pointer;
  letter-spacing: 1px;
  box-shadow: 0 3px #999;
  font-family: "Roboto", serif;
  text-align: center;
  margin-left: 1rem;

  &:hover {
    background-color: #a7d0cd;
  }
  &:active {
    background-color: #a4d4cc;
    box-shadow: 0 3px #666;
    transform: translateY(4px);
  }
  &:focus {
    outline: none;
  }
`;

const DetailsButtonCancel = styled.button`
  display: block;
  width: 50%;
  font-size: 1.2rem;
  border-radius: 3px;
  padding: 1rem 2rem;
  border: 1px solid black;
  background-color: white;
  color: black;
  text-transform: uppercase;
  cursor: pointer;
  letter-spacing: 1px;
  box-shadow: 0 3px #999;
  font-family: "Roboto", serif;
  text-align: center;
  margin-left: 1rem;

  &:hover {
    background-color: #a7d0cd;
  }
  &:active {
    background-color: #a4d4cc;
    box-shadow: 0 3px #666;
    transform: translateY(4px);
  }
  &:focus {
    outline: none;
  }
`;

const Modal = styled.div`
  display: ${(props) => (props.open ? "flex" : "none")};
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: 9999;
`;

const ModalContent = styled.div`
  padding: 20px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  max-height: 100vh;
  overflow-y: auto;
`;

const ModifyForm = ({ onCancel, orderId, orderDetails, totalPrice }) => {
  const [closeOrderForm, setCloseOrderForm] = useState(false);
  const [isEditFormVisible, setIsEditFormVisible] = useState(false);
  const [formVisible, setFormVisible] = useState(true);

  const handleCloseOrderDetails = () => {
    setCloseOrderForm(true);
  };

  const handleCloseDetails = () => {
    setCloseOrderForm(false);
  };

  const handleCloseOrder = () => {
    const orderID = {
      orderId: orderId,
    };

    ordersService
      .closeOrder(orderID)
      .then((response) => {
        onCancel();
      })
      .catch((error) => {
        console.error("Error al cerrar la orden:", error);
      });

    setCloseOrderForm(false);
    onCancel();
  };

  const handleAddProductsOrder = () => {
    setIsEditFormVisible(true);
  };

  const handleCloseEditOrderForm = () => {
    setIsEditFormVisible(false);
  };

  const handleAddedProducts = () => {
    setIsEditFormVisible(false);
    setFormVisible(false);
    onCancel();
  };
  return (
    <>
      {!isEditFormVisible && formVisible && (
        <Form className="form-react">
          <h1 style={{ fontSize: "1.7rem", marginBottom: "3%" }}>
            Order {orderId}
          </h1>
          <Buttons>
            <Button
              type="button"
              className="placeorder"
              onClick={handleAddProductsOrder}
            >
              Add Products
            </Button>
            <Button
              type="button"
              className="cancel"
              onClick={handleCloseOrderDetails}
            >
              Close Order
            </Button>
            <Button
              type="button"
              className="cancel"
              onClick={onCancel}
              style={{ marginTop: "20%" }}
            >
              Cancel
            </Button>
          </Buttons>

          {closeOrderForm ? (
            <Details>
              <DetailsContent>
                {orderDetails.map((productOrder) => (
                  <div key={productOrder.productOrderId}>
                    <strong>
                      {productOrder.quantity}x {productOrder.productName}
                    </strong>
                    <br />
                    <strong>${productOrder.productUnitPrice} ea.</strong>
                    <br />
                    <strong></strong>
                    <br />
                  </div>
                ))}
                <strong2 style={{ color: "white" }}>
                  Total price: ${totalPrice}
                </strong2>
                <div
                  style={{ display: "flex", justifyContent: "speace-between" }}
                >
                  <DetailsButton onClick={() => handleCloseOrder()}>
                    Close Order
                  </DetailsButton>
                  <DetailsButtonCancel onClick={() => handleCloseDetails()}>
                    Cancel
                  </DetailsButtonCancel>
                </div>
              </DetailsContent>
            </Details>
          ) : null}
        </Form>
      )}
      <Modal open={isEditFormVisible}>
        <ModalContent>
          {isEditFormVisible && (
            <EditOrderForm
              onCancel={handleCloseEditOrderForm}
              onAdd={handleAddedProducts}
              orderId={orderId}
            />
          )}
        </ModalContent>
      </Modal>
    </>
  );
};

export default ModifyForm;
