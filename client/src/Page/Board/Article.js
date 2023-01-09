import styled from "styled-components";

const userIcon = (
  <svg width="15" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512">
    <path
      fill="#0014FF"
      d="M224 256c70.7 0 128-57.3 128-128S294.7 0 224 0S96 57.3 96 128s57.3 128 128 128zm-45.7 48C79.8 304 0 383.8 0 482.3C0 498.7 13.3 512 29.7 512H322.8c-3.1-8.8-3.7-18.4-1.4-27.8l15-60.1c2.8-11.3 8.6-21.5 16.8-29.7l40.3-40.3c-32.1-31-75.7-50.1-123.9-50.1H178.3zm435.5-68.3c-15.6-15.6-40.9-15.6-56.6 0l-29.4 29.4 71 71 29.4-29.4c15.6-15.6 15.6-40.9 0-56.6l-14.4-14.4zM375.9 417c-4.1 4.1-7 9.2-8.4 14.9l-15 60.1c-1.4 5.5 .2 11.2 4.2 15.2s9.7 5.6 15.2 4.2l60.1-15c5.6-1.4 10.8-4.3 14.9-8.4L576.1 358.7l-71-71L375.9 417z"
    />
  </svg>
);

const Div = styled.div`
  display: flex;
  flex-direction: column;
  overflow: hidden;
  width: 80%;
  height: 120px;
  border-radius: 10px;
  border: solid 3px #eff5f5;
  margin: 30px auto 30px 0;

  h1 {
  }
  > .box {
    margin: 15px auto auto 30px;
  }
  span {
    padding-top: 10px;
  }
  span {
    color: #7c7c7c;
  }
  p {
    margin-top: 10px;
  }
`;

const Article = ({ data }) => {
  return (
    <Div>
      <div className="box">
        <h1>{data.title}</h1>
        <span>
          Writter &nbsp;
          {data.userId}
        </span>
        <p>{data.body.slice(0, 20)}... </p>
      </div>
    </Div>
  );
};

export default Article;
