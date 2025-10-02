terraform {
  backend "s3" {
    bucket = "pointtils-terraform-state-dev"
    key    = "terraform.tfstate"
    region = "us-east-2"
  }
}
