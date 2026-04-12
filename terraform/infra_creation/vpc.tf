# VPC Creation
resource "aws_vpc" "cicd-vpc" {
  cidr_block = "180.198.0.0/16"
  tags = {
    Name = "my-vpc"
  }
}
