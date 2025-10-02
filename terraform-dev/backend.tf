terraform {
  backend "s3" {
    bucket         = "pointtils-terraform-state-dev"
    key            = "terraform-dev.tfstate"
    region         = "us-east-2"
    encrypt        = true
    dynamodb_table = "pointtils-terraform-locks-dev"
  }
}
