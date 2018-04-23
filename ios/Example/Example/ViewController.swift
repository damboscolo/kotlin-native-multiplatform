//
//  ViewController.swift
//  Example
//
//  Created by Daniele Boscolo on 23/04/18.
//  Copyright © 2018 Tokenlab. All rights reserved.
//

import UIKit
import Common

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        print(CommonMain().sayHello())
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}

