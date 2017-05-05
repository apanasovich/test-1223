class Toolbar extends React.Component {
    render() {
        return (
            <h3>Chyo Kak? {this.props.name}</h3>
        );
    }
}

class App extends React.Component {
    render() {
        return (
            <div>
                <Toolbar name={this.props.name}/>
                <form action="/hello" method="get">
                    <label>Your 1st Name:<br/>
                        <input id="name" name="name"/>
                    </label>
                    <br/>
                    <label>Your Last Name:<br/>
                        <input id="lastName" name="lastName"/>
                    </label>
                    <br/>
                    <input type="submit"/>
                </form>
            </div>
        );
    }
}

ReactDOM.render(<App name="Zzz"/>, document.getElementById("root"));