import React, { Component } from 'react';
import Layout from '../../components/Layout/Layout';

class Login extends Component {
 constructor(props) {
    super(props);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const body = {
       host: 'lightapi.net',
       service: 'user',
       action: 'loginUser',
       version: '0.1.0',
       data: {nameOrEmail:formData.get('idoremail'), password: formData.get('password') },
     };
     alert('login submitted json: ' + JSON.stringify(body));
  }

  render() {
    return (

       <Layout>
           <p>Login Page Here</p>

           <form onSubmit={this.handleSubmit}>
           <table>
           <tbody>
           <tr><td>
             <label>
              UserId or Email:
             </label>
             </td>
             <td><input id="idoremail" name="idoremail" type="text" /></td>
           </tr>
           <tr><td>
             <label>
               Password:
             </label>
            </td>
               <td> <input id="password" name="password"  type="password" /></td>
              </tr>
             <tr>
            <td> <input type="submit" value="Login" /></td>
             <td><input type="reset" value="Reset" /></td>
             </tr>
           </tbody>
            </table>
           </form>
       </Layout>
    );
  }
}

export default Login;
