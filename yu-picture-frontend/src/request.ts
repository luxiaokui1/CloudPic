import axios from "axios";
import {message} from "ant-design-vue";

// Distinguish between development and production environments
const DEV_BASE_URL = "http://localhost:8123";
// const PROD_BASE_URL = "http://81.69.229.63";
// Create Axios instance
const myAxios = axios.create({
    baseURL: DEV_BASE_URL,
    timeout: 10000,
    withCredentials: true,
});

// Global request interceptor
myAxios.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    return config
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error)
  },
)

// Global response interceptor
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response
    // Not logged in
    if (data.code === 40100) {
      // If not a get-login-user request and not already on the login page, redirect to login
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('/user/login')
      ) {
        message.warning('Please log in first')
        window.location.href = `/user/login?redirect=${window.location.href}`
      }
    }
    return response
  },
  function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error)
  },
)

export default myAxios;
