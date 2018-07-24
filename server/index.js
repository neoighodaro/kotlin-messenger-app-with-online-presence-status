var express = require('express');
var bodyParser = require('body-parser');
const mongoose = require('mongoose');
var Pusher = require('pusher');

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

var pusher = new Pusher({
  appId: 'PUSHER_APP_ID',
  key: 'PUSHER_APP_KEY',
  secret: 'PUSHER_APP_SECRET',
  cluster: 'PUSHER_APP_CLUSTER'
});

mongoose.connect('mongodb://127.0.0.1/db');

const Schema = mongoose.Schema;
const userSchema = new Schema({
    name: { type: String, required: true, },
    count: {type: Number}
  });
var User = mongoose.model('User', userSchema);

userSchema.pre('save', function(next) {
    if (this.isNew) {
        User.count().then(res => {
          this.count = res; // Increment count
          next();
        });
      } else {
        next();
      }
});

// make this available to our users in our Node applications
module.exports = User;
var currentUser;


app.post('/login', (req, res) => {
    const myModel = mongoose.model('User');
    myModel.findOne({ name: req.body.name }, function (err, user) {
        if (err) {
            res.send("Error connecting to database");
        }
        if (user) {
            // user exists already
            currentUser = user;
            res.status(200).send(user)
        } else {
            // create new user
            var newuser = new User({
                name: req.body.name
              });
            newuser.save(function(err) {
                if (err) throw err;
                console.log('User saved successfully!');
            });
            currentUser = newuser;
            res.status(200).send(newuser)
        }
    });

})


// fetch all users
app.get('/users', (req, res) => {
    User.find({}, function(err, users) {
        if (err) throw err;
        // object of all the users
        res.send(users);
      });
})

// authenticate users for the presence channel
app.post('/pusher/auth/presence', (req, res) => {
    var socketId = req.body.socket_id;
    var channel = req.body.channel_name;
    var presenceData = {
      user_id: currentUser._id,
      user_info: {count: currentUser.count, name: currentUser.name}
    };

    res.send(pusher.authenticate(socketId, channel, presenceData));
});

// authenticate users for the private channel
app.post('/pusher/auth/private', (req, res) => {
    res.send(pusher.authenticate(req.body.socket_id, req.body.channel_name));
});

app.post('/send-message', (req, res) => {
    pusher.trigger(req.body.channel_name, 'new-message', {message: req.body.message,sender_id: req.body.sender_id});
    res.send(200);
});

var port = process.env.PORT || 5000;
app.listen(port);
