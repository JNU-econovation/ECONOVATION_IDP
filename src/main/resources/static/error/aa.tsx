// import axios from 'axios';
// import React, { useEffect, useState } from 'react';
// import './Login.css';
// import econoLogo from './images/econo_logo.png';
// import Spinner from './util/Spinner';
// import { useNavigate } from 'react-router';
// import useInput from './hook/useInput';
//
// const Login = () => {
//     const navigate = useNavigate();
//     const [input, inputUpdate] = useInput({email:'', password:''});
//     const [errorMessage, setErrorMessage] = useState('');
//     const [redirectUrl, setRedirectUrl] = useState('');
//     const [isLoading, setIsLoading] = useState(false);
//
//     const onSubmit = (e: any) => {
//         e.preventDefault();
//         if (!input.email || !input.password) {
//             setErrorMessage('아이디나 비밀번호를 정확히 입력해주세요.');
//             return;
//         }
//         setIsLoading(true);
//         const form = new FormData();
//         form.append('userEmail', input.email);
//         form.append('password', input.password);
//         form.append('redirectUrl', redirectUrl);
//         axios({
//             method: 'post',
//             baseURL: process.env.REACT_APP_SERVER_BASE_URL,
//             url: 'http://auth.econovation.kr:8080/api/accounts/login/process',
//             headers: {
//                 "Access-Control-Allow-Origin": `http://auth.econovation.kr:8080`,
//                 'Access-Control-Allow-Credentials':"true",
//             },
//             data: form,
//         })
//             .then((response) => {
//                 const { accessToken, refreshToken } = response.data;
//                 setIsLoading(false);
//                 if (response.status === 200) {
//                     localStorage.setItem('accessToken', accessToken);
//                     localStorage.setItem('refreshToken', refreshToken);
//                     localStorage.setItem('userEmail', input.email);
//                     window.location.href = redirectUrl;
//                 }
//             })
//             .catch((error) => {
//                 setIsLoading(false);
//                 setErrorMessage(
//                     () => error.response.data.message ?? '로그인에 실패했습니다.'
//                 );
//             });
//     };
//
//     const onSignUpClick = () => {
//         navigate('/signup');
//     };
//
//     useEffect(() => {
//         setRedirectUrl(
//             () => localStorage.getItem('redirectUrl') ?? 'https://econovation.kr'
//         );
//     }, []);
//
//     const onChange = ({ target: { name, value } }:any) => {
//         inputUpdate(name, value);
//     }
//
//     return (
//         <div className="card">
//             <img className="logo" src={econoLogo} alt="logo" />
//             <h2>Sign In</h2>
//             <form onSubmit={onSubmit} className="login-form">
//                 <input
//                     type="text"
//                     value={input.email}
//                     onChange={onChange}
//                     placeholder="아이디"
//                     name="email"
//                 />
//                 <input
//                     type="password"
//                     value={input.password}
//                     onChange={onChange}
//                     placeholder="비밀번호"
//                     name="password"
//                 />
//                 <button type="submit">{isLoading ? <Spinner /> : 'Sign In'}</button>
//                 <input type="button" value="회원가입" onClick={onSignUpClick} />
//                 <div className="error-message">{errorMessage}</div>
//             </form>
//         </div>
//     );
// };
//
// export default Login;