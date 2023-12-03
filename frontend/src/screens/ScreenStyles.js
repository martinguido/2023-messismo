import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-size: 1.5rem;
`;

export const MainContent = styled.div`
  display: ${(props) => (props.visible ? "flex" : "none")};
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  flex-grow: 1;
  font-size: 1.5rem;
`;
