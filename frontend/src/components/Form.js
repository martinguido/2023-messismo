import React, { useState, useEffect } from "react";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import "./Form.css";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select"; // { SelectChangeEvent }
import FormValidation from "../FormValidation";
import categoryService from "../services/category.service";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";

const Form = (props) => {
  const [name, setName] = useState("");
  // eslint-disable-next-line
  const [category, setCategory] = useState("");
  const [description, setDescription] = useState("");
  const [stock, setStock] = useState("");
  const [unitPrice, setUnitPrice] = useState("");
  const [errors, setErrors] = useState({});
  const [characterCount, setCharacterCount] = useState(0);
  const maxCharacterLimit = 255;
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [createNewCategory, setCreateNewCategory] = useState(false);
  const [cost, setCost] = useState("");

  useEffect(() => {
    categoryService
      .getAllCategories()
      .then((response) => {
        setCategories(response.data);
      })
      .catch((error) => {
        console.error("Error al obtener categorías:", error);
      });
  }, []);

  const handleNombreChange = (event) => {
    setName(event.target.value);
  };

  const handleStockChange = (event) => {
    setStock(event.target.value);
  };

  const handleCategoriaChange = (event) => {
    setSelectedCategory(event.target.value);
  };

  const handleDescripcionChange = (event) => {
    const text = event.target.value;
    setDescription(text);
    setCharacterCount(text.length);
  };

  const handlePrecioChange = (event) => {
    setUnitPrice(event.target.value);
  };

  const handleCostChange = (event) => {
    setCost(event.target.value);
  };

  const cancelarButton = (event) => {
    props.onClose();
  };

  const handleAddProduct = () => {
    const validationErrors = FormValidation({
      name,
      category: selectedCategory,
      price: unitPrice,
      cost: cost,
      stock,
    });

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
    } else {
      // const selectedCategoryObj = categories.find(cat => cat.name === selectedCategory);

      const newProductData = {
        name,
        newCategory: createNewCategory,
        category: selectedCategory,
        description,
        unitPrice,
        stock,
        unitCost: cost,
      };

      //productsService.addProducts(newProductData);
      props.onSave(newProductData);
      props.onClose();

      setName("");
      setCategory("");
      setDescription("");
      setUnitPrice("");
    }
  };
  const handleCreateNewCategoryChange = (event) => {
    setCreateNewCategory(event.target.checked);
  };

  return (
    <div>
      <h1 style={{ marginBottom: "5%", fontSize: "1.8rem" }}>New Product</h1>
      <p style={{ color: errors.name ? "red" : "black" }}>Name *</p>
      <TextField
        required
        id="name"
        value={name}
        onChange={handleNombreChange}
        variant="outlined"
        error={errors.name ? true : false}
        helperText={errors.name || ""}
        style={{
          width: "80%",
          marginTop: "3%",
          marginBottom: "3%",
          fontSize: "1.5rem",
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
      <p style={{ color: errors.category ? "red" : "black" }}>Category *</p>
      {!createNewCategory && (
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={selectedCategory}
          onChange={handleCategoriaChange}
          error={errors.category ? true : false}
          helperText={errors.category || ""}
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
        >
          {categories.map((category) => (
            <MenuItem key={category.id} value={category.name}>
              {category.name}
            </MenuItem>
          ))}
        </Select>
      )}
      <FormControlLabel
        control={
          <Checkbox
            checked={createNewCategory}
            onChange={handleCreateNewCategoryChange}
          />
        }
        label="Create new category"
      />
      {createNewCategory && (
        <TextField
          required
          id="name"
          value={selectedCategory}
          onChange={handleCategoriaChange}
          variant="outlined"
          error={errors.category ? true : false}
          helperText={errors.category || ""}
          style={{
            width: "80%",
            marginTop: "3%",
            marginBottom: "3%",
            fontSize: "1.5rem",
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
      )}
      <p>Description</p>
      <TextField
        required
        id="description"
        value={description}
        onChange={handleDescripcionChange}
        variant="outlined"
        style={{
          width: "80%",
          marginTop: "3%",
          marginBottom: "3%",
          fontSize: "1.5rem",
        }}
        InputProps={{
          style: {
            fontSize: "1.1rem",
          },
        }}
        inputProps={{
          maxLength: 255, // Establecer la longitud máxima permitida
        }}
      />
      <p
        style={{
          fontSize: "1rem",
          color: characterCount > maxCharacterLimit ? "red" : "black",
        }}
      >
        {characterCount}/{maxCharacterLimit}
      </p>
      <p style={{ color: errors.price ? "red" : "black" }}>Cost *</p>
      <TextField
        required
        id="unitCost"
        value={cost}
        onChange={handleCostChange}
        variant="outlined"
        style={{
          width: "80%",
          marginTop: "3%",
          marginBottom: "3%",
          fontSize: "1.3rem",
        }}
        error={errors.cost ? true : false}
        helperText={errors.cost || ""}
        InputProps={{
          style: {
            fontSize: "1.1rem",
            inputMode: "numeric",
            pattern: "[0-9]*",
          },
        }}
        FormHelperTextProps={{
          style: {
            fontSize: "1.1rem",
          },
        }}
      />
      <p style={{ color: errors.price ? "red" : "black" }}>Price *</p>
      <TextField
        required
        id="unitPrice"
        value={unitPrice}
        onChange={handlePrecioChange}
        variant="outlined"
        style={{
          width: "80%",
          marginTop: "3%",
          marginBottom: "3%",
          fontSize: "1.3rem",
        }}
        error={errors.price ? true : false}
        helperText={errors.price || ""}
        InputProps={{
          style: {
            fontSize: "1.1rem",
            inputMode: "numeric",
            pattern: "[0-9]*",
          },
        }}
        FormHelperTextProps={{
          style: {
            fontSize: "1.1rem",
          },
        }}
      />
      <p style={{ color: errors.stock ? "red" : "black" }}>Stock *</p>
      <TextField
        required
        id="stock"
        value={stock}
        onChange={handleStockChange}
        variant="outlined"
        style={{
          width: "80%",
          marginTop: "3%",
          marginBottom: "3%",
          fontSize: "1.3rem",
        }}
        error={errors.stock ? true : false}
        helperText={errors.stock || ""}
        InputProps={{
          style: {
            fontSize: "1.1rem",
            inputMode: "numeric",
            pattern: "[0-9]*",
          },
        }}
        FormHelperTextProps={{
          style: {
            fontSize: "1.1rem",
          },
        }}
      />
      <div className="buttons-add">
        <Button
          variant="outlined"
          style={{
            color: "grey",
            borderColor: "grey",
            width: "40%",
            fontSize: "1rem",
          }}
          onClick={cancelarButton}
        >
          Cancel
        </Button>
        <Button
          variant="contained"
          style={{
            backgroundColor: "#a4d4cc",
            color: "black",
            borderColor: "green",
            width: "40%",
            fontSize: "1rem",
          }}
          onClick={handleAddProduct}
        >
          Add
        </Button>
      </div>
    </div>
  );
};

export default Form;
