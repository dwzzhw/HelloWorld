import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

Future<Post> fetchPost() async {
  final response =
      await http.get('https://jsonplaceholder.typicode.com/posts/1');

  if (response.statusCode == 200) {
    return Post.fromJson(json.decode(response.body));
  } else {
    throw Exception('Fail to load post from net');
  }
}

class Post {
  final int userId;
  final int id;
  final String title;
  final String body;

  Post({this.userId, this.id, this.title, this.body});

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
        userId: json['userId'],
        id: json['id'],
        title: json['title'],
        body: json['body']);
  }
}

class DemoFetchDataFromNet extends StatelessWidget {
  static final routeName = 'fetchData';
  static final pageName = 'FetchDataFromNet';
  Future<Post> post;

  DemoFetchDataFromNet() {
    post = fetchPost();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Fetch data from Net"),
      ),
      body: Center(
        child: FutureBuilder<Post>(
            future: post,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                return Text('Title: ${snapshot.data.title}');
              } else if (snapshot.hasError) {
                return Text('Error: ${snapshot.error}');
              }

              return CircularProgressIndicator();
            }),
      ),
    );
  }
}
